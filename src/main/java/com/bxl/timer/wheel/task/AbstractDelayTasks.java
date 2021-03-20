package com.bxl.timer.wheel.task;

import com.bxl.timer.wheel.provider.TimeUnitProvider;
import com.bxl.timer.wheel.tool.MathTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 把tasks排成一个环，循环使用。通过index不断的往前滚动
 *
 * @author baixl
 * @date 2021/3/19
 */
public abstract class AbstractDelayTasks {
    //可以改成 双向链表 + 数组的结构：即节点存贮的对象中有指针，组成环形，可以通过数组的下标 灵活访问每个节点。类似 LinkedHashMap
    List<AbstractDelayTask> tasks = null;
    //当前的索引
    protected int index;
    protected int capacity;
    protected Integer level;

    private AtomicInteger num = new AtomicInteger(0);

    public AbstractDelayTasks(int capacity, Integer level) {
        this.capacity = capacity;
        this.level = level;
        this.tasks = new ArrayList<>(capacity);
        this.index = 0;
    }

    public AbstractDelayTask getTask() {
        return tasks.get(index);
    }

    public List<AbstractDelayTask> init() {
        long interval = MathTool.power((capacity + 1), level);
        long add = 0;
        AbstractDelayTask delayTask = null;
        for (int i = 0; i < capacity; i++) {
            add += interval;
            if (level == 0) {
                delayTask = new DefaultDelayTask(level);
            } else {
                delayTask = new SplitDelayTask(level);
            }
            //已经转换为最小的时间间隔
            delayTask.setDelay(add, TimeUnitProvider.getTimeUnit());
            tasks.add(delayTask);
        }
        return tasks;
    }

    public abstract void addTask(int interval, MyTask task);

    public void indexAdd() {
        this.index++;
        if (this.index >= capacity) {
            this.index = 0;
        }
    }

    public void addTask(AbstractDelayTask task) {
        tasks.add(task);
    }
}
