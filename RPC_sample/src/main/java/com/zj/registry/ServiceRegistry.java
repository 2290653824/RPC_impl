package com.zj.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

     void registry(String serviceName, InetSocketAddress inetSocketAddress);

}
