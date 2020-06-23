package com.tuling.sample.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class RedisController {
    @Autowired
    private Jedis jedis;
    @Autowired
    private JedisCluster jedisCluster;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/string")
    public String setAndGet(String key,String value){
        try {
            String ret = jedis.set(key, value);
            return ret;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * lua脚本实例
     * @return
     */
    @RequestMapping("/lua_test")
    public String luaTest(){
        try {
            jedis.set("product_stock_10016","15");
            String script = "local count = redis.call('get',KEYS[1])" +
                    "local a = tonumber(count)" +
                    "local b = tonumber(ARGV[1])" +
                    "if a >=b then" +
                    "redis.call('set',KEYS[1],a-b)" +
                    /*模拟抛出异常回滚
                    "bb ==0 " +*/
                    "return 1" +
                    "end" +
                    "return 0";
            Object eval = jedis.eval(script, Arrays.asList("product_stock_10016"), Arrays.asList("10"));
            log.info("--------->{}",eval);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return "end";
    }

    /**
     * 基于jedis的pipeline实例
     * @return
     */
    @RequestMapping("/pipeline")
    public String pipeline(){
        Pipeline pipeline = jedis.pipelined();
        for(int i=0;i<10;i++){
            pipeline.incr("pipelineKey");
            pipeline.set("plKey","plKey" + i);
        }
        List<Object> results = pipeline.syncAndReturnAll();
        log.info("{}",results);

        return "pipeline end";
    }

    @RequestMapping("/stringRedisTemplate")
    public String test1(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
        return "success";
    }

    @RequestMapping("/jedisCluster")
    public String jdisCluster(){
        try {
            String set = jedisCluster.set("jedisCluster", "jedisCluster");
            return set;
        }finally {
            if(jedisCluster != null){
                jedisCluster.close();
            }
        }
    }

    @RequestMapping("/bootCluster")
    public String bootCluster(){
        stringRedisTemplate.opsForValue().set("bootCluster","bootCluster");
        return "OK-------------------->";
    }
}
