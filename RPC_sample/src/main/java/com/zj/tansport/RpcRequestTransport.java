package com.zj.tansport;

import com.zj.dto.RpcRequest;
import com.zj.dto.RpcResponse;

public interface RpcRequestTransport {

     RpcResponse sendRpcRequest(RpcRequest rpcRequest);
}