package com.zj.registry.zookeeper;

import com.zj.dto.RpcRequest;
import com.zj.registry.balance.LoadBalance;
import com.zj.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZKServiceDiscovery implements ServiceDiscovery {

    private  LoadBalance loadBalance;

    @Override
    public InetSocketAddress discovery(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(zkClient, serviceName);
        if(CollectionUtils.isEmpty(childrenNodes)){
            log.error("this server list is empty");
        }
        log.info("the serviceName list:[{}]",childrenNodes);
        String serviceAddress = loadBalance.selectServiceAddress(childrenNodes, rpcRequest);
        log.info("this balance has successfully get the target address");
        String[] hostAndPort=serviceAddress.split(":");
        return new InetSocketAddress(hostAndPort[0],Integer.valueOf(hostAndPort[1]));
    }
}
