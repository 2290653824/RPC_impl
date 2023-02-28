package com.zj.controller;


import com.zj.dto.RpcServiceConfig;
import com.zj.service.HelloServiceImpl;
import com.zj.spring.RpcScan;
import com.zj.tansport.netty.impl.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.zj"})
public class NettyServerMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext application = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer rpcServer = application.getBean("nettyRpcServer", NettyRpcServer.class);
        RpcServiceConfig config = RpcServiceConfig.builder().service(new HelloServiceImpl())
                .version("version1").group("test1").build();

        rpcServer.register(config);
        rpcServer.start();

    }
}
