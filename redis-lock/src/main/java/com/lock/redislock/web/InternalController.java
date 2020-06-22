package com.lock.redislock.web;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
