package com.zj.registry.zookeeper;

import com.zj.constant.RpcConfigEnum;
import com.zj.utils.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtils {


    public final static String ZK_REGISTRY_ROOT_PATH="/my_rpc";

    private final static String DEFAULT_ZOOKEEPER_ADDRESS="localhost:2181";

    private final static int BASE_SLEEP_TIME=1000; // 重试等待时间

    private final static int MAX_RETRIES=3; //连接重试次数

    private static CuratorFramework zkClient;

    private final static Set<String> REGISTRYED_PATH_SET= ConcurrentHashMap.newKeySet();

    private final static Map<String,List<String>> SERVICE_ADDRESS_MAP=new ConcurrentHashMap<>();

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

    public static List<String> getChildrenNodes(CuratorFramework zkClient,String serviceName){
        if(SERVICE_ADDRESS_MAP.containsKey(serviceName)){
            return SERVICE_ADDRESS_MAP.get(serviceName);
        }

        List<String> result=null;
        String servicePath=ZK_REGISTRY_ROOT_PATH +"/"+ serviceName;
        try{
             result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName,result);
            registerWatcher(serviceName, zkClient);
        } catch (Exception e) {
           log.error("get children nodes for path [{}] fail",servicePath);
        }
        return result;
    }


    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        // 构建服务节点的路径
        String servicePath = ZK_REGISTRY_ROOT_PATH + "/" + rpcServiceName;

        // 创建一个 PathChildrenCache 对象，用于监听服务节点下的子节点变化
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);

        // 创建一个 PathChildrenCache 监听器，用于处理节点变化事件
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            // 获取服务节点下的所有子节点，并将它们的地址保存到 SERVICE_ADDRESS_MAP 中
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };

        // 将 PathChildrenCache 监听器添加到 PathChildrenCache 中
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);

        // 启动 PathChildrenCache，开始监听服务节点下的子节点变化
        pathChildrenCache.start();
    }
}
