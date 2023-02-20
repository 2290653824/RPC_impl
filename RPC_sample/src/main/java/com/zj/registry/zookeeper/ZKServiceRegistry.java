package com.zj.registry.zookeeper;

import com.zj.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

public class ZKServiceRegistry implements ServiceRegistry {

    @Override
    public void registry(String serviceName, InetSocketAddress inetSocketAddress) {
        String path=CuratorUtils.ZK_REGISTRY_ROOT_PATH+"/"+serviceName+inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient,path);

    }
}
