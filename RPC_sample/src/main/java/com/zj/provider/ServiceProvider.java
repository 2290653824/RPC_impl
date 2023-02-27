package com.zj.provider;


import com.zj.dto.RpcServiceConfig;

public interface ServiceProvider {

    /**
     * 添加service,仅仅是将相关的service关系加载到java部分。
     * @param rpcServiceConfig
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * 根据rpcServiceName拿到相应的service对象
     * @param rpcServiceName
     * @return
     */
    Object getService(String rpcServiceName);

    /**
     * 将rpcServiceName发不到zk
     * @param rpcServiceConfig
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

}
