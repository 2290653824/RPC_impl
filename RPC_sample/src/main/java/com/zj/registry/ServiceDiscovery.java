package com.zj.registry;

import com.zj.dto.RpcRequest;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    InetSocketAddress discovery(RpcRequest rpcRequest);
}
