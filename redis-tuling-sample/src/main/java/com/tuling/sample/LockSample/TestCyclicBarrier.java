package com.tuling.sample.LockSample;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [来个全套]
 *
 * @slogan: 高于生活，源于生活
 * @Description: TODO
 * @author: smlz
 * @date 2020/6/11 16:55
 */
public class TestCyclicBarrier {

    public static String[] heros = new String[]{"王昭君","李白","狄仁杰","韩信","亚索",
                                                "百里守约","伽罗","兰陵王","程咬金","周瑜"};

    private static ExecutorService service = Executors.newFixedThreadPool(10);


    public static void main(String[] args) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(10, new Runnable() {
            @Override
            public void run() {
                System.out.println("所有英雄加载完毕。。。。全军出击:"+ Arrays.asList(heros));
            }
        });

        for(int count=0;count<heros.length;count++) {
            service.execute(new Player(heros[count],cyclicBarrier));
        }

        service.shutdown();
    }

}

@Slf4j
class Player implements Runnable{

    private String heroNames;

    private CyclicBarrier cyclicBarrier;

    public Player(String heroNames,CyclicBarrier cb) {
        this.heroNames = heroNames;
        this.cyclicBarrier = cb;
    }

    @SneakyThrows
    @Override
    public void run() {
        int loadTime = new Random().nextInt(5);
        log.info("当前英雄:{}开始加载...",heroNames);
        //模拟加载耗时loadTime
        Thread.sleep(loadTime);
        log.info("当前英雄:{}加载完毕...等待其他英雄加载",heroNames);
        cyclicBarrier.await();
    }
}
