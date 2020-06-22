package com.lock.redislock.web;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
public class TraditionalController {
    /*@Autowired
    private Redisson redisson;*/

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 1、不加锁会存在超卖的问题
     * @return
     */
    @RequestMapping("/traditional_deduct_stock")
    public String deductStock(){
        /**
         * 一、无锁状态,并发下可能出现超卖
         */
        /*int  stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));//jedis.get("stock")从redis获取库存
        if(stock > 0){
            int realStock = stock - 1;
            stringRedisTemplate.opsForValue().set("stock",realStock + "");
            log.info("扣减成功，剩余库存:{}",realStock);
        }else{
            log.info("扣减失败，库存不足");
        }
        return "业务处理完成";*/
        /**
         * 二、单jvm加synchronized或者ReentrantLock控制,分布式情况依然后超卖
         */
        /*synchronized (this){
            int  stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));//jedis.get("stock")从redis获取库存
            if(stock > 0){
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock",realStock + "");
                log.info("扣减成功，剩余库存:{}",realStock);
            }else{
                log.info("扣减失败，库存不足");
            }
            return "业务处理完成";
        }*/

        /**
         * 三：分布式场景1
         * 1:抛异常不执行，加try{}finally{},在finally里面执行
         * 2：如果try{}代买快没执行完就宕机了，形成死锁，怎么解决？那么需要加超时时间
         * 3：添加了超时时间，如果在加完锁，但是还没有设置超时时间的时候宕机，又会形成死锁，怎么解决呢？
         * stringRedisTemplate提供了在设置锁的同时添加超时时间的原子操作
         * 3.1：如果业务逻辑还没有执行完成，但是锁到期时间已经到了，那么redis会删除锁，这个时候锁不存在了，那么其它线程又能加锁成功了
         * 但是这个时候它本身的锁已经过期被删除了，它执行完成的时候删除锁的时候，就会删除第二个线程加的锁，当第三个线程又拿到所，第二个
         * 线程又把第三个线程的锁删除掉，如果是高并发的情况下，那么这个时候就会造成锁失效（一个线程删除了别的线程的锁），那怎么解决?
         * 解决思路：针对不同的线程，设置一个唯一的value，并且在删除锁的时候判断redis锁里面的获取到value和当前的clientId是否一致，如果一致才能删除
         * 还可能存在的问题：设置超时时间多少合适？如果在设定的时间内业务逻辑没有完成，那么过期时间也会把锁删除，其它线程又能拿到锁
         */
        String lockKey = "product_001";
        String clientId = UUID.randomUUID().toString();
        try {
            /*Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "deduct_stock");//加锁
            stringRedisTemplate.expire(lockKey,10, TimeUnit.SECONDS);//添加超时时间*/
            //加锁+添加超时时间
            Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(lockKey,clientId,30,TimeUnit.SECONDS);
            if(!isLock){
                return "errorCode：1001";
            }
            int  stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));//jedis.get("stock")从redis获取库存
            if(stock > 0){
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock",realStock + "");
                log.info("扣减成功，剩余库存:{}",realStock);
            }else{
                log.info("扣减失败，库存不足");
            }
        }finally {
            //如果当前的clientId跟redis里面获取到的clientId相等，才允许删除
            if(clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))){
                stringRedisTemplate.delete(lockKey);
            }
        }
        return "业务处理完成";
    }

}
