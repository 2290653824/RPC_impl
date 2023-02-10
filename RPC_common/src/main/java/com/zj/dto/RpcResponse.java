package com.zj.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class RpcResponse<T> {
    private String requestId;
    private int code;
    String message;
    private T data;
}
