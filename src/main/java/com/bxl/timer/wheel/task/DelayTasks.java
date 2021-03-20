package com.bxl.timer.wheel.task;

/**
 * 把tasks排成一个环，循环使用。通过index不断的往前滚动
 *
 * @author baixl
 * @date 2021/3/19
 */
public class DelayTasks extends AbstractDelayTasks {

    public DelayTasks(int capacity, Integer level) {
        super(capacity, level);
    }

    public synchronized void addTask(int interval, MyTask task) {
        interval -= 1;
        int curIndex = interval + this.index;
        if (curIndex >= capacity) {
            curIndex = curIndex - capacity;
        }
        tasks.get(curIndex).addTask(task);
    }
}
