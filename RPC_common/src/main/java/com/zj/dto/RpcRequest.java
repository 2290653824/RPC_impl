package com.zj.dto;

import lombok.*;

import javax.annotation.security.DenyAll;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class RpcRequest {

    String interfaceName;
    String methodName;
}
