package com.bxl.timer.wheel.task;

import com.bxl.timer.wheel.TimerWheel;
import com.bxl.timer.wheel.provider.TimeUnitProvider;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/19
 */
public class SplitDelayTask extends AbstractDelayTask {
    public SplitDelayTask(int level) {
        super(level);
    }

    //到时间执行所有的任务
    public void run() {
        List<MyTask> tasks = getTasks();
        if (CollectionUtils.isNotEmpty(tasks)) {
            tasks.forEach(task -> {
                long delay = task.getDelay();
                TimerWheel.adddTask(task,delay, TimeUnitProvider.getTimeUnit());
            });
        }
    }
}
