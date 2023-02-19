package com.zj.tansport.netty.test;

import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse response = (RpcResponse) msg;
        log.info("client get RpcResponse:{}",response); //客户端通过调用rpc收到response信息
        AttributeKey<RpcResponse> key=AttributeKey.valueOf("rpcResponse");
        ctx.channel().attr(key).set(response);
        ctx.channel().close();

    }
}
