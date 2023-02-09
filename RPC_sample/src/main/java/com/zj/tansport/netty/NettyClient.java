package com.zj.tansport.netty;

import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import com.zj.serialize.kryo.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class NettyClient {

    String host;
    String port;
    static Bootstrap bootstrap;

    public NettyClient(String host,String port){
        this.port=port;
        this.host = host;
    }
    static{
        bootstrap=new Bootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(nioEventLoopGroup)
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


}
