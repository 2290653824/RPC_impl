package com.zj.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

public class CustomScanner extends ClassPathBeanDefinitionScanner {

    /**
     * 就是将指定的注解的类加载到ioc中
     * @param registry
     * @param annoType
     */
    public CustomScanner(BeanDefinitionRegistry registry,Class<? extends Annotation> annoType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }



    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
