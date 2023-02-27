package com.zj.registry.balance.impl;

import com.zj.dto.RpcRequest;
import com.zj.registry.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
@Slf4j
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {
        if(CollectionUtils.isEmpty(serviceUrlList)){
            return null;
        }
        if(serviceUrlList.size()==1){
            return serviceUrlList.get(0);
        }
        return doSelectServiceAddress(serviceUrlList,rpcRequest);
    }

    protected String doSelectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest){
        log.error("the loadBalance doSelectServiceAddress has failed,this reason is loadBalance has not being implimented");
        throw new RuntimeException("the loadBalance doSelectServiceAddress has failed");
    }
}
