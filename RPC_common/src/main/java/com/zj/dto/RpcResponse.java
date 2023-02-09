package com.zj.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class RpcResponse {
    String message;
}
