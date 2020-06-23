package com.lock.redislock.web;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class InternalController {
    @Autowired
    private Redisson redisson;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/internal_deduct_stock")
    public String deductStock(){
        String lockKey = "product_001";
        //获取锁,redisson会自动创建
        RLock lock = redisson.getLock(lockKey);
        try {
            //加锁，实现锁续命的功能
            lock.lock();
            int  stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if(stock > 0){
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock",realStock + "");
                log.info("扣减成功，剩余库存:{}",realStock);
            }else{
                log.info("扣减失败，库存不足");
            }
        }finally {
            //释放锁
            lock.unlock();
        }

        return "业务处理完成";
    }

    @RequestMapping("/redLock")
    public String redLock() throws InterruptedException {
        String lockKey = "product_001";
        //这里需要实例化不同的redis实例的redisson客户端连接，练习所以就用一个实例示意了
        RLock rLock1 = redisson.getLock(lockKey);
        RLock rLock2 = redisson.getLock(lockKey);
        RLock rLock3 = redisson.getLock(lockKey);
        //根据多个Rlock对象构建
        RedissonRedLock lock = new RedissonRedLock(rLock1,rLock2,rLock3);
        try {
            /**
             * 尝试获取锁，waitTime表示获取锁的最大等待时间，超过该设置，则判定获取锁失败
             * leaseTime表示持有锁的时间，超过这个时间锁会自动失效（要准确评估业务代码的执行时间，并且设置大于评估时间的值）
             * 锁没有自动续命功能，在高并发场景下，存在安全问题，不推介使用
             */
            boolean ret = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if(ret){
                //处理业务逻辑
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "业务处理完成";
    }
}
