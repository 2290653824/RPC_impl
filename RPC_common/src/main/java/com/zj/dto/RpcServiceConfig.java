package com.zj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 包含三类信息
 * version、group、service
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RpcServiceConfig {

    String version;

    String group;

    Object service;

    public String getRpcServiceName(){
        return this.getServiceName()+getGroup()+getVersion();
    }


    public String getServiceName(){
        return service.getClass().getInterfaces()[0].getCanonicalName(); //https://zhuanlan.zhihu.com/p/45672773 注意这里获取名字
    }


}
