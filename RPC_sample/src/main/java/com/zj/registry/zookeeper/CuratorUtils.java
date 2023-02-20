package com.zj.registry.zookeeper;

import com.zj.constant.RpcConfigEnum;
import com.zj.utils.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtils {


    public final static String ZK_REGISTRY_ROOT_PATH="/my_rpc";

    private final static String DEFAULT_ZOOKEEPER_ADDRESS="localhost:8080";

    private final static int BASE_SLEEP_TIME=1000; // 重试等待时间

    private final static int MAX_RETRIES=3; //连接重试次数

    private static CuratorFramework zkClient;

    private final static Set<String> REGISTRYED_PATH_SET= ConcurrentHashMap.newKeySet();

    public static CuratorFramework getZkClient(){
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String zookeeperAddress=
                properties!=null&&properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue())!=null?
                        properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()):DEFAULT_ZOOKEEPER_ADDRESS;
        if(zkClient!=null&&zkClient.getState()== CuratorFrameworkState.STARTED){
            return zkClient;
        }
        log.info("开始创建zkClient");
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient= CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();

        zkClient.start(); //启动一个客户端
        try{
            if(!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)){
                throw new RuntimeException("Time out waiting to connect to ZK");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;

    }

    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try{
            if(REGISTRYED_PATH_SET.contains(path)||zkClient.checkExists().forPath(path)!=null){
                log.info("the node already exists. the node is:[{}]",path);
            }else{
                //建立持久节点的方法
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("the node was created successfully . the node is [{}]",path);
            }
            REGISTRYED_PATH_SET.add(path);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
