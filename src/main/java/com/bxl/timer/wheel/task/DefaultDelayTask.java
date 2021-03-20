package com.bxl.timer.wheel.task;

import com.bxl.timer.wheel.thread.ThreadPool;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/19
 */
public class DefaultDelayTask extends AbstractDelayTask {
    public DefaultDelayTask(int level) {
        super(level);
    }

    //到时间执行所有的任务
    public void run() {
        List<MyTask> tasks = getTasks();
        if (CollectionUtils.isNotEmpty(tasks)) {
            tasks.forEach(task -> ThreadPool.submit(task));
        }
    }
}
