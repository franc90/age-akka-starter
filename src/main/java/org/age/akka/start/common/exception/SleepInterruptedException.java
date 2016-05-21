package org.age.akka.start.common.exception;

public class SleepInterruptedException extends RuntimeException {

    public SleepInterruptedException(Throwable cause) {
        super(cause);
    }

}
