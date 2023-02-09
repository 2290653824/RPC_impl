package com.zj.serialize;

import java.io.FileNotFoundException;

public interface Serializer {

    /**
     * 序列化
     * @param obj
     * @return
     */
    byte[] serialize(Object obj) throws FileNotFoundException;

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
