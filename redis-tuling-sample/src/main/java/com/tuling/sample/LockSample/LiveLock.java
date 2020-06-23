package com.tuling.sample.LockSample;

import java.util.Random;

/**
 * Created by Administrator on 2020/6/8.
 */
public class LiveLock {

    //按摩小姐姐
    private static  BeautyGile beautyGirl = new BeautyGile();

    public static void main(String[] args) {

        Thread yangguo = new Thread(new Runnable() {
            @Override
            public void run() {
                servicing(false,200);
            }
        },"yanggo");

        Thread smlz = new Thread(new Runnable() {
            @Override
            public void run() {
                servicing(false,200);
            }
        },"smlz");

        yangguo.start();

        smlz.start();

    }


    public static void servicing(boolean servicingFlag,int money) {

        synchronized (beautyGirl) {
            //还没有开始服务
            while (!servicingFlag) {
                try {
                    //小姐姐开始服务
                    beautyGirl.servicing(money);
                    //正常服务,把状态表示为true
                    servicingFlag = true;
                }catch (Exception e) {
                    String customerName= Thread.currentThread().getName();
                    System.out.println("顾客:"+customerName+"当前出价为: "+money);

                    //模拟顾客出价
                    try {
                        Thread.sleep(1000);
                        money = new Random().nextInt(399);
                        System.out.println("顾客"+customerName+"思考后的价格: "+money);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        }
    }
}

class BeautyGile {

    //开始服务的价格是399,低于399的拒绝服务(模拟抛出异常)
    private static Integer beginServicePrice = 399;

    public void servicing(int money) throws Exception {
        if(money<beginServicePrice) {
            throw new Exception("服务的价格:"+money +"<399 拒绝服务");
        }
    }
}
