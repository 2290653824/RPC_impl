package com.zj.tansport.netty.impl.client;

import com.zj.constant.CompressTypeEnum;
import com.zj.constant.RpcConstants;
import com.zj.dto.RpcMessage;
import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import com.zj.registry.ServiceDiscovery;
import com.zj.registry.zookeeper.ZKServiceDiscovery;
import com.zj.tansport.RpcRequestTransport;
import com.zj.tansport.netty.impl.ChannelProvider;
import com.zj.tansport.netty.impl.UnprocessedRequests;
import com.zj.tansport.netty.impl.server.NettyRpcServerHandler;
import com.zj.tansport.netty.impl.server.RpcMessageDecoder;
import com.zj.tansport.netty.impl.server.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j

public final class NettyRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery; //服务发现

    private final UnprocessedRequests unprocessedRequests;

    private final ChannelProvider channelProvider;

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient(){
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap=new Bootstrap().group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new NettyRpcServerHandler());
                    }
                });

        this.serviceDiscovery = new ZKServiceDiscovery();
        this.unprocessedRequests = new UnprocessedRequests();
        this.channelProvider = new ChannelProvider();



    }

    /**
     * 知识点：
     * 1. @SneakyThrows
     * 2. 客户端连接需要什么
     * 3. CompletableFuture
     * 4. netty中的addListener
     * @param inetSocketAddress
     * @return
     */
    @SneakyThrows //lombok插件可以自动生成try catch并向上抛出
    public Channel doConnection(InetSocketAddress inetSocketAddress){
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener)future->{
            if(future.isSuccess()){
                log.info("this client has connected [{}] successful",inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            }else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }


    /**
     * future的使用
     * channel怎么发送消息，并感知消息是否发送
     * @param rpcRequest
     * @return
     */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> completableFuture = new CompletableFuture<>();
//        String rpcServiceName=rpcRequest.toRpcProperties().toRpcServiceName(); //根据请求查看服务名称
        InetSocketAddress inetSocketAddress=serviceDiscovery.discovery(rpcRequest);//根据名称通过 负载均衡找到对应的地址端口
        Channel channel = doConnection(inetSocketAddress);
        if(channel.isActive()){
            //将每个对应的requestId存到一个容器中
            unprocessedRequests.UNPROCESSED_RESPONSE.put(rpcRequest.getRequestId(),completableFuture);
            //将发送信息进行封装到rpcMessage中。future发送失败时，会返回cause信息
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setData(rpcRequest);
//            rpcMessage.setRequestId(rpcMessage.getRequestId());
            rpcMessage.setCodec(CompressTypeEnum.GZIP.getCode());
            rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
            rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener)future->{
                if(future.isSuccess()){
                    log.info("client sent message [{}} successful",rpcMessage);
                }else{
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    log.error("send fail:[{}]",future.cause().toString());
                }
            });
        }else{
            throw new IllegalStateException();
        }
        return completableFuture;

    }
}