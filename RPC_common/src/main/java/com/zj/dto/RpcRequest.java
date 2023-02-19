package com.zj.dto;

import lombok.*;

import javax.annotation.security.DenyAll;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class RpcRequest {
    String requestId;
    String interfaceName;
    String methodName;
    private Object[] parameters;
    private Class<?> parameterType;
    private String version;
    private String group;  //解决一个实现类有多个接口
}
