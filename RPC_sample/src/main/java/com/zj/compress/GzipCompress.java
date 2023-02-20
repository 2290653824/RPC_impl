package com.zj.compress;

import com.esotericsoftware.kryo.util.Null;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompress implements Compress{

    private static final int BUFFER_SIZE=1024*4;

    /**
     * 原生的java如何进行压缩的
     * 重点是如何理解这几个out与in
     * @param bytes
     * @return
     */
    @Override
    public byte[] compress(byte[] bytes) {
        if(bytes==null){
            throw new NullPointerException("bytes is null");
        }
        try(ByteArrayOutputStream out=new ByteArrayOutputStream();
            GZIPOutputStream gzip=new GZIPOutputStream(out)){
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip compress failed",e);
        }
    }

    /**
     * 重点理解
     * @param bytes
     * @return
     */
    @Override
    public byte[] deCompress(byte[] bytes) {
        if(bytes==null){
            throw new NullPointerException("bytes is null");
        }

        try(ByteArrayOutputStream out=new ByteArrayOutputStream();
            GZIPInputStream in=new GZIPInputStream(new ByteArrayInputStream(bytes))){
            byte[] buffer=new byte[BUFFER_SIZE];
            int n;
            while((n=in.read(buffer))>-1){
                out.write(buffer,0,n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
