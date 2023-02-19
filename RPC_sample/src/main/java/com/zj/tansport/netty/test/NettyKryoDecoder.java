package com.zj.tansport.netty.test;

import com.zj.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {

    Serializer serializer;
    Class<?> genericClazz;

    public static final int HEAD_LENGTH=4;

    public NettyKryoDecoder(Serializer serializer,Class< ? > genericClazz){
        this.serializer = serializer;
        this.genericClazz = genericClazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()>HEAD_LENGTH){
            byteBuf.markReaderIndex();
            int len=byteBuf.readInt();
            if(len<0||byteBuf.readableBytes()<0){
                log.error("数据错误");
                return;
            }
            if(byteBuf.readableBytes()<len){
                byteBuf.resetReaderIndex();
                return;
            }
            byte[] bytes = new byte[len];
            byteBuf.readBytes(bytes);
            Object obj = serializer.deserialize(bytes, genericClazz);
            list.add(obj);
        }
    }
}
