package com.zj.compress;


/**
 * 压缩：序列化后进行压缩
 */
public interface Compress {

    byte[] compress(byte[] bytes);

    byte[] deCompress(byte[] bytes);


}
