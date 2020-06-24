package com.tuling.sample.web;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;
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

    @Autowired
    private BloomFilter<String> bloomFilter;


    @PostConstruct
    public void init(){
        //将所有数据存入布隆过滤器
        String[] keys = new String[2000];
        for(String key : keys){
            bloomFilter.put(key);
        }
    }

    /**
     * 测试在布隆过滤器中是否存在
     */
    @RequestMapping("/bloomTest")
    public String bloomTest(String key){
        //1、从布隆过滤器这一级缓存判断下key是否存在
        boolean exists = bloomFilter.mightContain(key);
        if(!exists){
            return "查询的商品不存在";
        }
        //2、从redis缓存中获取
        String cacheValue = "";
        if(StringUtils.isEmpty(cacheValue)){
            //3、从数据库获取
            //3.1、从数据库查询
            //3.2、将查询到的数据放入redis缓存
            //3.3、设置缓存过期时间
            return "数据库获取到的数据是";
        }else{
            return cacheValue;
        }
    }
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
