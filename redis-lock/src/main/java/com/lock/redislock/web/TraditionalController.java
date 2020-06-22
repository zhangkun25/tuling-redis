package com.lock.redislock.web;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/traditional")
public class TraditionalController {
    @Autowired
    private Redisson redisson;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @RequestMapping("/deduct_stock")
    public String deductStock(){
        //从redis获取库存
        int  stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));

        if(stock > 0){
            int realStock = stock - 1;
            stringRedisTemplate.opsForValue().set("stock",realStock + "");
            log.info("扣减成功，剩余库存:{}",realStock);
        }else{
            log.info("扣减失败，库存不足");
        }
        return null;
    }

}
