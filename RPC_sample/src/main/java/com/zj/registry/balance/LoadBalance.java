package com.zj.registry.balance;

import com.zj.dto.RpcRequest;

import java.util.List;

public interface LoadBalance {

    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
