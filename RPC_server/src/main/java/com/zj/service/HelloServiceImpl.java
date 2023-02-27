package com.zj.service;


public class HelloServiceImpl implements HelloService{
    @Override
    public String startHello() {
        return "this is server ,you have successfully receive this rpc message";
    }
}
