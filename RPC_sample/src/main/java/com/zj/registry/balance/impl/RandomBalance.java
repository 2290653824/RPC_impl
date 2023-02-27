package com.zj.registry.balance.impl;

import com.zj.dto.RpcRequest;
import com.zj.registry.balance.LoadBalance;

import java.util.List;
import java.util.Random;

public class RandomBalance extends AbstractLoadBalance{
    @Override
    protected String doSelectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {
        int size=serviceUrlList.size();
        Random random = new Random();
        int index = random.nextInt(size);
        return serviceUrlList.get(index);
    }
}
