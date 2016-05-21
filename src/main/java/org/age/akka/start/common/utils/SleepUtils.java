package org.age.akka.start.common.utils;

import org.age.akka.start.common.exception.SleepInterruptedException;

import java.util.concurrent.TimeUnit;

public class SleepUtils {

    public static void sleep(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            throw new SleepInterruptedException(e);
        }
    }

}
