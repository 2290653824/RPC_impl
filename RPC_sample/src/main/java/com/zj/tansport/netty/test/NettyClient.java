package com.zj.tansport.netty.test;

import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import com.zj.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class NettyClient {

    String host;
    int port;
    static Bootstrap bootstrap;

    public NettyClient(String host,int port){
        this.port=port;
        this.host = host;
    }
    static{
        bootstrap=new Bootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap=bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyKryoDecoder(new KryoSerializer(), RpcResponse.class));
                        socketChannel.pipeline().addLast(new NettyKryoEncoder(new KryoSerializer(), RpcRequest.class));
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    public RpcResponse sent(RpcRequest rpcRequest) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        Channel channel = channelFuture.channel();
        log.info("已经连接成功，开始发送信息");
        System.out.println("已经连接成功，开始发送信息");
        channel.writeAndFlush(rpcRequest);

        channel.closeFuture().sync();
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        return channel.attr(key).get();

    }

    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient("localhost", 8089);
        RpcRequest request = RpcRequest.builder().interfaceName("UserService").methodName("getUserById").build();
        for(int i=3;i>0;i--){
            RpcResponse sent = client.sent(request);
            System.out.println(sent);
        }


    }


}
