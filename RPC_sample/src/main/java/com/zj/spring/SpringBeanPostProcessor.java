package com.zj.spring;

import com.zj.dto.RpcServiceConfig;
import com.zj.provider.ServiceProvider;
import com.zj.provider.ServiceProviderImpl;
import com.zj.proxy.RpcClientProxy;
import com.zj.tansport.netty.impl.client.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    ServiceProvider serviceProvider;

    public SpringBeanPostProcessor(){
        serviceProvider=new ServiceProviderImpl();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(RpcService.class)){
            log.info("bean [{}] has annotated with rpcService",beanName);
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder().service(bean)
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .build();
            serviceProvider.publishService(rpcServiceConfig);

        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(RpcReference.class)){
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                if(rpcReference!=null){
                    RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder().version(rpcReference.version())
                            .group(rpcReference.group()).build();
                    RpcClientProxy rpcClientProxy = new RpcClientProxy(new NettyRpcClient(),rpcServiceConfig);
                    Object proxy = rpcClientProxy.getProxy(field.getType());
                    field.setAccessible(true);
                    try {
                        field.set(bean,proxy);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return bean;
    }
}
