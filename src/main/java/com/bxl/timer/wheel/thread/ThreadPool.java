package com.bxl.timer.wheel.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/19
 */
public class ThreadPool {
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(10000),
            new MyThreadFactory("executor"), new ThreadPoolExecutor.CallerRunsPolicy());

    private static ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1,
            new MyThreadFactory("scheduled"), new ThreadPoolExecutor.CallerRunsPolicy());

    static class MyThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        MyThreadFactory(String prefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = prefix + "-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    public static void submit(Runnable r) {
        executor.submit(r);
    }

    public static void scheduled(Runnable r, long initialDelay,
                                 long period,
                                 TimeUnit unit) {
        scheduled.scheduleAtFixedRate(new MyTask(r), initialDelay, period, unit);
    }

    static class MyTask implements Runnable {
        private Runnable r;

        public MyTask(Runnable r) {
            this.r = r;
        }

        public void run() {
            executor.submit(r);
        }
    }
}
