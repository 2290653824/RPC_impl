package com.zj.registry.zookeeper;

import com.zj.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

public class ZKServiceRegistry implements ServiceRegistry {

    @Override
    public void registry(String serviceName, InetSocketAddress inetSocketAddress) {
        //my_rpc/serviceName
        String path=CuratorUtils.ZK_REGISTRY_ROOT_PATH+"/"+serviceName+inetSocketAddress.toString();
        //获得zk
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        //创建持久化节点
        CuratorUtils.createPersistentNode(zkClient,path);

    }
}
