package com.tuling.sample.jedis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class JedisConfig {
    private final static String REDIS_SERVER="192.168.222.121";
    private final static int REDIS_PORT=6379;
    private final static int TIME_OUT=3000;

    @Bean
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(5);
        return jedisPoolConfig;
    }

    /**
     * 单节点配置
     * @return
     */
    /*@Bean
    public JedisPool jedisPool(){
        return new JedisPool(jedisPoolConfig(),REDIS_SERVER,REDIS_PORT,TIME_OUT,null);
    }

    @Bean
    public Jedis jedis(){
        return jedisPool().getResource();
    }*/
    /**
     * 哨兵配置
     */

    /**
     * 集群配置
     * @return
     */
    @Bean
    public Set<HostAndPort> clusterNodes(){
        Set<HostAndPort> clusterNodes = new HashSet<>();
        clusterNodes.add(new HostAndPort("192.168.222.150",8001));
        clusterNodes.add(new HostAndPort("192.168.222.150",8002));
        clusterNodes.add(new HostAndPort("192.168.222.150",8003));

        clusterNodes.add(new HostAndPort("192.168.222.151",8004));
        clusterNodes.add(new HostAndPort("192.168.222.151",8005));
        clusterNodes.add(new HostAndPort("192.168.222.151",8006));

        clusterNodes.add(new HostAndPort("192.168.222.152",8007));
        clusterNodes.add(new HostAndPort("192.168.222.152",8008));
        clusterNodes.add(new HostAndPort("192.168.222.152",8009));
        return  clusterNodes;
    }

    @Bean
    public JedisCluster jedisCluster(){
        return new JedisCluster(clusterNodes(),6000,5000,10,"111111",jedisPoolConfig());
    }
}
