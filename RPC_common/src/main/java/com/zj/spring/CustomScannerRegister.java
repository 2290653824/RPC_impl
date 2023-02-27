package com.zj.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

@Slf4j
public class CustomScannerRegister implements ImportBeanDefinitionRegistrar {

    private static final String BASE_PACKAGES_ATTRIBUTE_NAME="basePackage";

    /**
     * 什么是resourceLoader？
     */
    private ResourceLoader resourceLoader;

    public CustomScannerRegister(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        //通过importingClassMetadata获取我们想要的注解，这里我们想要的是RpcScan注解
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        //拿到注解中对应的属性的值
        if(attributes != null){
            rpcScanBasePackages = attributes.getStringArray(BASE_PACKAGES_ATTRIBUTE_NAME);
        }
        //如果我们没有拿到对应的值，那么我们就设置一个默认的值。
        /**
         * 因此，((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName() 返回了注解所在类的包名。
         * 这段代码主要是用于获取扫描的包名。如果注解上未指定扫描的包名，则会扫描注解所在类所在的包。
         */
        if(rpcScanBasePackages.length==0){
            rpcScanBasePackages=new String[]{((StandardAnnotationMetadata)annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }

        CustomScanner rpcServiceCustomScanner = new CustomScanner(registry, RpcService.class);
        CustomScanner componentCustomScanner = new CustomScanner(registry, Component.class);
        if(resourceLoader!=null){
            rpcServiceCustomScanner.setResourceLoader(resourceLoader);
            componentCustomScanner.setResourceLoader(resourceLoader);
        }
        int rpcServiceCount = rpcServiceCustomScanner.scan(rpcScanBasePackages);
        log.info("扫描到的rpcService类有 [{}] 个",rpcServiceCount);
        int componentCount = componentCustomScanner.scan(rpcScanBasePackages);
        log.info("扫描到的component类有 [{}] 个",componentCount);

    }
}
