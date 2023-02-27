package com.zj.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegister.class)
public @interface RpcScan {
    String[] basePackage() default "";
}
