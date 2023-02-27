package com.zj.utils;

public class RuntimeUtil {

    /**
     * 获取当前cpu的数量
     * @return
     */
    public static int cpus(){
        return Runtime.getRuntime().availableProcessors();
    }
}
