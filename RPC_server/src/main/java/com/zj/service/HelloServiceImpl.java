package com.zj.service;

import com.zj.spring.RpcService;

@RpcService(group = "test1",version = "version1")
public class HelloServiceImpl implements HelloService{
    @Override
    public String startHello() {
        return "this is server ,you have successfully receive this rpc message";
    }
}
