package com.zj.tansport.netty.test;

import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        log.info("sever get request:[{}]",request);
        RpcResponse response = RpcResponse.builder().message("hello ,server has recieved your message").build();
        ctx.writeAndFlush(response);
    }
}
