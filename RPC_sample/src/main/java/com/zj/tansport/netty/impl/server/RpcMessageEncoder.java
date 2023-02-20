package com.zj.tansport.netty.impl.server;

import com.zj.compress.Compress;
import com.zj.compress.GzipCompress;
import com.zj.constant.CompressTypeEnum;
import com.zj.constant.RpcConstants;
import com.zj.constant.SerializationTypeEnum;
import com.zj.dto.RpcMessage;
import com.zj.serialize.Serializer;
import com.zj.serialize.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//    /**
//     * Returns the {@code writerIndex} of this buffer.
//     */
//    public abstract int writerIndex();
//
//    /**
//     * Sets the {@code writerIndex} of this buffer.
//     *
//     * @throws IndexOutOfBoundsException
//     *         if the specified {@code writerIndex} is
//     *            less than {@code this.readerIndex} or
//     *            greater than {@code this.capacity}
//     */
//    public abstract ByteBuf writerIndex(int writerIndex);
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder {


    private Class<?> aClass;

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        try {
            byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER); //4B
            byteBuf.writeByte(RpcConstants.VERSION); //1B
            byteBuf.writerIndex(byteBuf.writerIndex() + 4);//暂时跳过消息长度字段 4B
            RpcMessage rpcMessage = (RpcMessage) o;
            int type = rpcMessage.getMessageType();
            byteBuf.writeByte(type); //1B 为什么这里可以直接从message中获取
            int codec = rpcMessage.getCodec();
            byteBuf.writeByte(codec); //1B 为什么这里可以直接从message中获取 先序列化 后压缩，为什么不设置到message中呢？

            byteBuf.writeByte(CompressTypeEnum.GZIP.getCode()); //为什么这里是直接从枚举类中拿，而不从message中拿呢？ 1B
            byteBuf.writeInt(ATOMIC_INTEGER.getAndIncrement()); //requestId? 4B

            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            if (type != RpcConstants.HEARTBEAT_RESPONSE_TYPE && type != RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                String codecName= SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}]",codecName);
//                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
//                        .getExtension(codecName);  TODO:这是什么获取方式
                Serializer serializer=new KryoSerializer();
                bodyBytes= serializer.serialize(rpcMessage.getData());
//                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
//                        .getExtension(compressName); TODO:这是什么获取方式
                Compress compress=new GzipCompress(); //对序列化后的数据进行压缩
                bodyBytes = compress.compress(bodyBytes);
                fullLength+=bodyBytes.length;
            }
            if(bodyBytes!=null) byteBuf.writeBytes(bodyBytes);

            int writeIndex=byteBuf.writerIndex(); //当前的写索引位置
            byteBuf.writerIndex(writeIndex-fullLength+RpcConstants.MAGIC_NUMBER.length+1);
            byteBuf.writeInt(fullLength); //在这里才真正写入长度字段
            byteBuf.writerIndex(writeIndex); //将写指针归位
        } catch (Exception e) {
            log.error("encode process failed ,this exception:[{}]", e.toString());
        }

    }
}
