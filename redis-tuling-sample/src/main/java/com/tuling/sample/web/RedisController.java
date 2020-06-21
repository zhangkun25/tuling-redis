package com.tuling.sample.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

@Slf4j
@RestController
public class RedisController {
    /*@Autowired
    private Jedis jedis;*/
    @Autowired
    private JedisCluster jedisCluster;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /*@RequestMapping("/string")
    public String setAndGet(String key,String value){
        String ret = jedis.set(key, value);
        return ret;
    }*/

    @RequestMapping("/stringRedisTemplate")
    public String test1(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
        return "success";
    }

    @RequestMapping("/jdisCluster")
    public String jdisCluster(){
        try {
            String set = jedisCluster.set("jedisCluster", "jedisCluster");
            return set;
        }finally {
            jedisCluster.close();
        }
    }

    @RequestMapping("/bootCluster")
    public String bootCluster(){
        stringRedisTemplate.opsForValue().set("bootCluster","bootCluster");
        return "OK-------------------->";
    }
}
