package com.zj.registry.zookeeper;

import com.zj.dto.RpcRequest;
import com.zj.registry.LoadBalance;
import com.zj.registry.ServiceDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceDiscovery implements ServiceDiscovery {

    private  LoadBalance loadBalance;

    @Override
    public InetSocketAddress discovery(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(zkClient, serviceName);
        if(CollectionUtils.isEmpty(childrenNodes)){
            throw new
        }
        loadBalance.selectServiceAddress(childrenNodes,rpcRequest);


    }
}
