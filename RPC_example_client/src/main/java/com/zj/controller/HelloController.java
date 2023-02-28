package com.zj.controller;

import com.zj.service.HelloService;
import com.zj.spring.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HelloController {

    @RpcReference(version = "version1",group = "test1")
    private HelloService helloService;


    public String hello(){
        return helloService.startHello();
    }

    public void test() {
        HelloController helloController = new HelloController();
        String hello = helloController.hello();
        log.info("this client get result is [{}]",hello);
    }
}
