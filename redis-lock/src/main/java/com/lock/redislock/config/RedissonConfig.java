package com.lock.redislock.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    public Redisson redisson(){
        Config config = new Config();
        //单机模式
        config.useSingleServer().setAddress("redis://192.168.222.121:6379").setDatabase(0);
        //集群模式
        /*config.useClusterServers()
                .addNodeAddress("redis://192.168.222.150:8001")
                .addNodeAddress("redis://192.168.222.150:8002")
                .addNodeAddress("redis://192.168.222.150:8003")
                .addNodeAddress("redis://192.168.222.151:8004")
                .addNodeAddress("redis://192.168.222.151:8005")
                .addNodeAddress("redis://192.168.222.151:8006")
                .addNodeAddress("redis://192.168.222.152:8007")
                .addNodeAddress("redis://192.168.222.152:8008")
                .addNodeAddress("redis://192.168.222.152:8009");*/

        return (Redisson) Redisson.create(config);
    }
}
