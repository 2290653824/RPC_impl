package com.zj.tansport.netty.test;

import com.zj.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyKryoEncoder extends MessageToByteEncoder {

    Serializer serializer;
    Class<?> genericClass;

    public NettyKryoEncoder(Serializer serializer,Class genericClass) {
        this.serializer=serializer;
        this.genericClass=genericClass;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(genericClass.isInstance(o)){
            byte[] serialize = serializer.serialize(o);
            int length=serialize.length;
            byteBuf.writeInt(length);  //先写长度
            byteBuf.writeBytes(serialize);
        }
    }
}
