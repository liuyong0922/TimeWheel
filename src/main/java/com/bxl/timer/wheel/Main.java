package com.bxl.timer.wheel;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/19
 */
public class Main {
    private static DelayQueue<TestTask> queue = new DelayQueue<TestTask>();

    private static class TestTask implements Delayed {
        private Long delay;

        public TestTask(Long delay) {
            this.delay = delay + TimeUnit.SECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return this.delay - TimeUnit.SECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long d = (getDelay(TimeUnit.SECONDS) - o.getDelay(TimeUnit.SECONDS));
            return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
        }

        public void test() {
            //System.out.println("test");
        }
    }

    public static void main(String[] args) {
        Random random = new Random();
        long start = System.currentTimeMillis();
        TimerWheel.adddTask(new MyTask(27, TimeUnit.SECONDS, 1001), (long) 27, TimeUnit.SECONDS);
        TimerWheel.adddTask(new MyTask(67, TimeUnit.SECONDS, 1002), (long) 67, TimeUnit.SECONDS);
        TimerWheel.adddTask(new MyTask(1000, TimeUnit.SECONDS, 1003), (long) 1000, TimeUnit.SECONDS);
        for (int i = 0; i < 1000; i++) {
            int interval = random.nextInt(2700);
            TimerWheel.adddTask(new MyTask(interval, TimeUnit.SECONDS, i), (long) interval, TimeUnit.SECONDS);
        }
        System.out.println("use time" + (System.currentTimeMillis() - start));
        System.out.println("111");
   /* Thread say =new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    TestTask take = queue.take();
                    if(take != null){
                        take.test();
                    }
                } catch (InterruptedException e) {

                }
            }
        }
    });
        say.setDaemon(false);
        say.start();
        long start = System.currentTimeMillis();
        for(int i=0;i<100000000;i++){
            int interval = random.nextInt(31);
            queue.add(new TestTask((long)interval));
        }
        System.out.println("use time"+(System.currentTimeMillis()-start));
        System.out.println("111");*/


    }

    static class MyTask extends com.bxl.timer.wheel.task.MyTask {
        private long startTime;
        public MyTask(long interval, TimeUnit unit, int index) {
            super(interval, unit, index);
            this.startTime = new Date().getTime();
        }

        @Override
        public void run() {
            System.out.println(String.format("index : %s ; interval: %s ; executeTime : %s", index, interval, (new Date().getTime() - this.startTime)));
        }
    }
}
