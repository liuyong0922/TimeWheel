package com.bxl.timer.wheel.task;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/19
 */
@Getter
public abstract class AbstractDelayTask implements Delayed {

    private List<MyTask> tasks = new ArrayList<MyTask>();
    private int level;
    private Long delay;
    
    private long calDelay;
    private TimeUnit calUnit;

    public AbstractDelayTask(int level) {
        this.level = level;
    }

    /**
     * 延时时间，根据设置的最小单位计算
     * @param delay
     * @param unit
     */
    public void setDelay(Long delay, TimeUnit unit) {
        this.calDelay=delay;
        this.calUnit=unit;
       
    }
    
    public void calDelay() {
        this.delay = TimeUnit.NANOSECONDS.convert(this.calDelay, this.calUnit) + System.nanoTime(); 
    }
    public long getDelay(TimeUnit unit) {
        return this.delay - System.nanoTime();
    }

    public int compareTo(Delayed o) {
        long d = (getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
        return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
    }

    public void addTask(MyTask task) {
        synchronized (this) {
            tasks.add(task);
        }
    }

    public void clear() {
        tasks.clear();
    }

    public abstract void run();
}
