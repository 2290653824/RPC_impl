package com.zj.tansport.netty.impl.handler;

import com.zj.dto.RpcResponse;
import com.zj.tansport.netty.impl.UnprocessedRequests;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private  UnprocessedRequests unprocessedRequests;

    /**
     * 1.消息进入handler后我们应该怎么处理信息
     * 2.ReferenceCountUtil.release()其实是ByteBuf.release()方法
     * （从ReferenceCounted接口继承而来）的包装。netty4中的ByteBuf使用了引用计数（
     * netty4实现了一个可选的ByteBuf池），每一个新分配的ByteBuf的引用计数值为1，
     * 每对这个ByteBuf对象增加一个引用，需要调用ByteBuf.retain()方法，
     * 而每减少一个引用，需要调用ByteBuf.release()方法。
     * 当这个ByteBuf对象的引用计数值为0时，表示此对象可回收。
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{

            log.info("client has recieved this message[{}]",msg);
            if(msg instanceof RpcResponse){
                RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg;
                unprocessedRequests.complete(rpcResponse);
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
