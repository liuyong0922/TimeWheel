package com.bxl.timer.wheel.provider;

import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @author baixl
 * @date 2021/3/20
 */
public class TimeUnitProvider {
    private static TimeUnit unit = TimeUnit.SECONDS;

    public static TimeUnit getTimeUnit() {
        return unit;
    }
}
