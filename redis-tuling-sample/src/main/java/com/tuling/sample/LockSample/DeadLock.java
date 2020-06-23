package com.tuling.sample.LockSample;

import lombok.extern.slf4j.Slf4j;

/**
 * 死锁产生的条件：
 * 1、2个或者2个以上的线程同时争抢多个资源
 * 2、多个线程之间抢到资源的顺序不同
 * 3、多个线程互相不释放抢到的资源
 * 打破死锁：
 * 1、每个线程按照相同顺序获取资源
 * 2、如果存在互相持有对方需要的资源时，释放已持有的资源，重新争抢
 * 3、保证一次拿到所需要的所有资源
 */
@Slf4j
public class DeadLock {
    static String technician13 = "13号技师，擅长头部按摩";
    static String technician14 = "14号技师，擅长足底按摩";

    public static void main(String[] args) throws InterruptedException {
        log.info("司马往柜台一丢SVIP金卡：'来个全套'，前台接待员屁颠屁颠跑过来：哟，客官.....");
        Thread sima = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (technician13){
                    log.info("司马操作失误，左手拉住13号的同时铁头功攻击失误，将14号推到杨过面前");
                    synchronized (technician14){
                        log.info("郁闷中.......................");
                    }
                }
            }
        });

        log.info("杨过紧跟司马后脚进来了,玄铁重剑往台子上一丢：特么的，有活人么.....：'来个全套'");
        log.info("前台接待员吓得直打哆嗦：大...大...大爷");
        log.info("杨过司马对视一秒，同时暴怒出手-----Round one....");
        log.info("接待员叫出technician13和technician14救场");
        log.info("杨过司马很默契的又对视一秒.........");
        Thread yangguo = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (technician14){
                    log.info("只见那杨过单手一抬,将14号拥入怀中");
                    synchronized (technician13){
                        log.info("纳闷中...............");
                    }
                }
            }
        });

         log.info("高手过招，就在一瞬间");
         sima.start();
         //Thread.sleep(10);
         yangguo.start();
    }
}
