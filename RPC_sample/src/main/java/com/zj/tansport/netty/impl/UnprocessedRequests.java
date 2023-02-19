package com.zj.tansport.netty.impl;

import com.zj.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放还没有被服务端处理的请求
 */
@Slf4j
public class UnprocessedRequests {

    /**
     * 并发map
     * 设计巧妙
     */

    public static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE=new ConcurrentHashMap<>();

    public void complete(RpcResponse<Object> rpcResponse){
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE.remove(rpcResponse.getRequestId());
        if(future!=null){
            future.complete(rpcResponse);
            log.info("response message [{}] has completely set in future and remove from UNPROCESSED_RESPONSE",rpcResponse);
        }else{
            throw new IllegalStateException();
        }

    }

    public void put(String requestId,CompletableFuture<RpcResponse<Object>> completableFuture){
        UNPROCESSED_RESPONSE.put(requestId,completableFuture);
    }
}
