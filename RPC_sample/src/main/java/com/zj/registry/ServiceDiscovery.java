package com.zj.registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    InetSocketAddress discovery(String serviceName);
}
