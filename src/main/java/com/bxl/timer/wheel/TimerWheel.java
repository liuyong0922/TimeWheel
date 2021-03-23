package com.bxl.timer.wheel;

import com.bxl.timer.wheel.provider.TimeUnitProvider;
import com.bxl.timer.wheel.task.AbstractDelayTask;
import com.bxl.timer.wheel.task.DelayTasks;
import com.bxl.timer.wheel.task.MyTask;
import com.bxl.timer.wheel.thread.ThreadPool;
import com.bxl.timer.wheel.tool.MathTool;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/19
 */
public class TimerWheel {
    private static Map<Integer, DelayTasks> cache = new ConcurrentHashMap<>();
    //一个轮表示三十秒
    private static int interval = 30;
    private static wheelThread wheelThread;

    public static  void adddTask(MyTask task, Long time, TimeUnit unit) {
        if(task == null){
            return;
        }
        long intervalTime = TimeUnitProvider.getTimeUnit().convert(time, unit);
        if(intervalTime < 1){
            ThreadPool.submit(task);
            return;
        }
        Integer[] wheel = getWheel(intervalTime,interval);
        DelayTasks taskList = cache.get(wheel[0]);
        if (taskList != null) {
            taskList.addTask(wheel[1], task);
        } else {
            synchronized (cache) {
                if (cache.get(wheel[0]) == null) {
                    taskList = new DelayTasks(interval-1, wheel[0]);
                    wheelThread.add(taskList.init());
                    cache.putIfAbsent(wheel[0],taskList);
                }
            }
            taskList.addTask(wheel[1], task);
        }

    }
    static{
        interval = 30;
        wheelThread = new wheelThread();
        wheelThread.setDaemon(false);
        wheelThread.start();
    }
    private static Integer[] getWheel(long intervalTime,long baseInterval) {
        //转换后的延时时间
        if (intervalTime < baseInterval) {
            return new Integer[]{0, Integer.valueOf(String.valueOf((intervalTime % 30)))};
        } else {
            return getWheel(intervalTime,baseInterval,baseInterval, 1);
        }
    }

    private static Integer[] getWheel(long intervalTime,long baseInterval,long interval, int p) {
         long nextInterval = baseInterval * interval;
        if (intervalTime < nextInterval) {
            return new Integer[]{p, Integer.valueOf(String.valueOf(intervalTime / interval))};
        } else {
            return getWheel(intervalTime,baseInterval,nextInterval, (p+1));
        }
    }

    static class wheelThread extends Thread {
        DelayQueue<AbstractDelayTask> queue = new DelayQueue<AbstractDelayTask>();

        public DelayQueue<AbstractDelayTask> getQueue() {
            return queue;
        }

        public void add(List<AbstractDelayTask> tasks) {
            if (CollectionUtils.isNotEmpty(tasks)) {
                tasks.forEach(task -> add(task));
            }
        }

        public void add(AbstractDelayTask task) {
            task.calDelay();
            queue.add(task);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    AbstractDelayTask task = queue.take();
                    int p = task.getLevel();
                    long nextInterval = MathTool.power(interval, Integer.valueOf(String.valueOf(MathTool.power(2, p))));
                    DelayTasks delayTasks = cache.get(p);
                    synchronized (delayTasks) {
                        delayTasks.indexAdd();
                        task.run();
                        task.clear();
                    }
                    task.setDelay(nextInterval, TimeUnitProvider.getTimeUnit());
                    task.calDelay();
                    queue.add(task);
                } catch (InterruptedException e) {

                }
            }
        }
    }

}
