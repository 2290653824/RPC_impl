package com.zj.tansport.netty.impl.server;

import com.zj.utils.RuntimeUtil;
import com.zj.utils.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcServer {

    public static final int PORT=9998;

    @SneakyThrows
    public void start(){
//        CustomShutdownHook.getCustomShutdownHook().clearAll();
        String host= InetAddress.getLocalHost().getHostAddress();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1); //处理连接请求
        NioEventLoopGroup workGroup = new NioEventLoopGroup(); //处理对话请求
        //这里的线程是如何决定的
        //从这里可以知道这个项目是IO密集的项目，所以这里的cpu数量设置为了2倍
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(RuntimeUtil.cpus()*2
        , ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false));

        try{
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p=socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup,new NettyRpcServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(host, PORT).sync();
            f.channel().closeFuture().sync();
        }catch (InterruptedException e){
            log.error("occur exception when start server:",e);
        }finally {
            log.error("shutdown server");
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }

}
