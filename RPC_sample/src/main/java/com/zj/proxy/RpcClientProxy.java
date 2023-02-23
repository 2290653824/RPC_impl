package com.zj.proxy;

import com.zj.constant.RpcConstants;
import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

@Slf4j
public class RpcClientProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("rpcClient execute proxy method name is [{}],args is [{}]",method,args);
        RpcRequest.builder().methodName(method.getName())
                .interfaceName(method.getDeclaringClass().getName()) //获取类名
                .parameters(args)
                .parameterType(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .version()
                .group();

        //通过网络通信将rpcRequest通过网络通信发送给服务方 TODO

        RpcResponse<Object> rpcResponse= null;

        return rpcResponse.getData();
    }

    /**
     * 获取一个代理对象
     * @param clazz
     * @return
     * @param <T>
     */
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }
}
