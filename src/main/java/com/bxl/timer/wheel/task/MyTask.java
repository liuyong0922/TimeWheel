package com.bxl.timer.wheel.task;

import com.bxl.timer.wheel.provider.TimeUnitProvider;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/20
 */
public abstract class MyTask implements Runnable {
     protected long interval;
     protected int index;
     protected long executeTime;
     public MyTask(long interval, TimeUnit unit, int index) {
          this.interval  = interval;
          this.index = index;
          this.executeTime= TimeUnitProvider.getTimeUnit().convert(interval,unit)+TimeUnitProvider.getTimeUnit().convert(System.nanoTime(),TimeUnit.NANOSECONDS);
     }
     public long getDelay() {
          return this.executeTime - TimeUnitProvider.getTimeUnit().convert(System.nanoTime(), TimeUnit.NANOSECONDS);
     }
}
