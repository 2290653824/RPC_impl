package com.zj.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import com.zj.serialize.Serializer;

import java.io.*;

public class KryoSerializer implements Serializer {

    private ThreadLocal<Kryo> threadLocal= ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) throws FileNotFoundException {
        Kryo kryo = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            kryo = threadLocal.get();
            Output output = new Output(byteArrayOutputStream);
            kryo.writeObject(output, obj);
            threadLocal.remove(); //防止内存溢出
            return output.toBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            Kryo kryo = threadLocal.get();
            Input input = new Input(byteArrayInputStream);
            T res = kryo.readObject(input, clazz);
            threadLocal.remove(); //防止内存溢出
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) throws FileNotFoundException {
//        RpcRequest rpcRequest = new RpcRequest();
//        KryoSerializer kryoSerializer = new KryoSerializer();
//        byte[] serialize = kryoSerializer.serialize(rpcRequest);
//
//        RpcRequest deserialize = kryoSerializer.deserialize(serialize, RpcRequest.class);
//    }
}


