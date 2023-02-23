package com.zj.provider;

import com.zj.dto.RpcServiceConfig;
import com.zj.registry.zookeeper.ZKServiceRegistry;
import com.zj.tansport.netty.impl.server.NettyRpcServer;
import com.zj.tansport.netty.test.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceProviderImpl implements ServiceProvider{

    private ZKServiceRegistry zkServiceRegistry;
    private Map<String,Object> map;  //存储rpcServiceName与service的映射关系
    private Set<String> registeredServiceName; //已经注册过的rpcServiceName

    public ServiceProviderImpl(){
        zkServiceRegistry=new ZKServiceRegistry();
        map=new ConcurrentHashMap<>();
        registeredServiceName=ConcurrentHashMap.newKeySet();
    }

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName=rpcServiceConfig.getRpcServiceName();
        if(registeredServiceName.contains(rpcServiceName)){
            return;
        }
        registeredServiceName.add(rpcServiceName);
        map.put(rpcServiceName,rpcServiceConfig.getService());

    }

    @Override
    public Object getService(String rpcServiceName) {
        if(registeredServiceName.contains(rpcServiceName)){
            return map.get(rpcServiceName);
        }
        log.error("[{}] has not be registered in zk ",rpcServiceName);
        return null;

    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig){
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("publicService has failed ,error:[{}]",e.getMessage());
        }
        this.addService(rpcServiceConfig);
        zkServiceRegistry.registry(rpcServiceConfig.getServiceName(),new InetSocketAddress(host, NettyRpcServer.PORT));
    }
}
