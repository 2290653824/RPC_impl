package com.zj.tansport.netty.impl;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelProvider {
    private  final Map<String, Channel> channelMap;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress inetSocketAddress){
        String key=inetSocketAddress.toString();
        Channel channel = channelMap.get(key);
        if(channel!=null&&channel.isActive()){
            return channel;
        }else{
            remove(inetSocketAddress);
        }
        return null;
    }

    public void remove(InetSocketAddress inetSocketAddress){
        channelMap.remove(inetSocketAddress.toString());
        log.info("channel[{}] remove from channelMap,now chanelMap=[{}]",inetSocketAddress.toString(),channelMap);
    }

}
