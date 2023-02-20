package com.zj.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class PropertiesFileUtil {

    private PropertiesFileUtil(){

    }

    /**
     * 怎么读取一个配置文件
     * @param fileName
     * @return
     */
    public static Properties readPropertiesFile(String fileName){
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath="";
        if(url!=null){
            rpcConfigPath=url.getPath()+fileName;
        }
        Properties properties=null;
        try(InputStreamReader inputStreamReader=new InputStreamReader(
                new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8
        )){
            properties=new Properties();
            properties.load(inputStreamReader);
        } catch (FileNotFoundException e) {
            log.error("occur exception when read properties file [{}]", fileName);
        } catch (IOException e) {
            log.error("occur exception when read properties file [{}]", fileName);
        }
        return properties;
    }
}
