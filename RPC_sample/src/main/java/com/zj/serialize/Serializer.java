package com.zj.serialize;

public interface Serializer {

    /**
     * 序列化
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
