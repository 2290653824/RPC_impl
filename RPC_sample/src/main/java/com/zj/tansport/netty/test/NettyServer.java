package com.zj.tansport.netty.test;

import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import com.zj.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

    private final int port;
    private  static ServerBootstrap serverBootstrap;
    public NettyServer(int port) {
        this.port = port;
    }
    static {
        serverBootstrap=new ServerBootstrap();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        serverBootstrap = serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyKryoEncoder(new KryoSerializer(), RpcResponse.class));
                        socketChannel.pipeline().addLast(new NettyKryoDecoder(new KryoSerializer(), RpcRequest.class));
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                });

    }

    public void run(){
        serverBootstrap.bind(port);
    }

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer(8089);
        System.out.println("start to bind");
        log.info("start to bind");
        nettyServer.run();
    }


}
