package com.zj.tansport.netty.impl.server;

import com.zj.compress.Compress;
import com.zj.compress.GzipCompress;
import com.zj.constant.CompressTypeEnum;
import com.zj.constant.RpcConstants;
import com.zj.constant.SerializationTypeEnum;
import com.zj.dto.RpcMessage;
import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import com.zj.serialize.Serializer;
import com.zj.serialize.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * 知识点：
 * 1.解码器的创建
 * 2.协议的自定义：规定服务端和客户端之间发送信息的协议
 * 3.具体协议：   魔数4、版本1、消息长度4、消息类型1、压缩类型1、序列化类型1、请求id4、消息体
 */

/**
 * 这里使用的是 LengthFieldBasedFrameDecoder
 * maxFrameLength：最大帧长度。也就是可以接收的数据的最大长度。如果超过，此次数据会被丢弃。
 * lengthFieldOffset：长度域偏移。就是说数据开始的几个字节可能不是表示数据长度，需要后移几个字节才是长度域。
 * lengthFieldLength：长度域字节数。用几个字节来表示数据长度。
 * lengthAdjustment：数据长度修正。因为长度域指定的长度可以使header+body的整个长度，也可以只是body的长度。如果表示header+body的整个长度，那么我们需要修正数据长度。
 * initialBytesToStrip：跳过的字节数。如果你需要接收header+body的所有数据，此值就是0，如果你只想接收body数据，那么需要跳过header所占用的字节数。
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {


    public RpcMessageDecoder(){
        this(RpcConstants.MAX_FRAME_LENGTH,5,4,-9,0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }


    /**
     * 不是太明白是什么意思
     * @param ctx
     * @param in
     * @return
     * @throws Exception
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if(decoded instanceof ByteBuf){
            ByteBuf frame=(ByteBuf) decoded;
            if(frame.readableBytes()>=RpcConstants.TOTAL_LENGTH){
                try{
                    return decodeFrame(frame);
                }catch (Exception e){
                    log.error("Decode frame error!",e);
                    throw e;
                }finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength=in.readInt();

        byte messageType = in.readByte();
        byte codecType=in.readByte();
        byte compressType=in.readByte();
        int requestId=in.readInt();
        RpcMessage rpcMessage =RpcMessage.builder()
                .codec(codecType).requestId(requestId).messageType(messageType).build();

        if(messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE){
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if(messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE){
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }

        int bodyLength = fullLength-RpcConstants.HEAD_LENGTH;
        if(bodyLength > 0){
            byte[] bs=new byte[bodyLength];
            in.readBytes(bs);

            String compressName = CompressTypeEnum.getName(compressType);
            //这里应该用反射是最好的
            //Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
            //                    .getExtension(compressName);
            Compress compress=new GzipCompress();
            bs = compress.deCompress(bs);

            String codecName= SerializationTypeEnum.getName(codecType);
            //本应该可以使用反射来得到的
            Serializer serializer=new KryoSerializer();
            if(messageType==RpcConstants.REQUEST_TYPE){
                RpcRequest rpcRequest = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(rpcRequest);
            }else{
                RpcResponse rpcResponse = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(rpcResponse);
            }

        }

        return rpcMessage;
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if(version!=RpcConstants.VERSION){
            throw new RuntimeException("version isn't compatible "+version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] temp=new byte[len];
        in.readBytes(temp);
        for(int i=0;i<len;i++){
            if(temp[i]!=RpcConstants.MAGIC_NUMBER[i]){
                throw new IllegalStateException("Unknown magic code:"+ Arrays.toString(temp));
            }
        }

    }


}
































