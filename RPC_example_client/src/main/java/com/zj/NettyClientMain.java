package com.zj;

import com.zj.spring.RpcScan;
import com.zj.controller.HelloController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = "com.zj")
public class NettyClientMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext
                = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController)applicationContext.getBean("helloController");
        helloController.test();
    }
}
