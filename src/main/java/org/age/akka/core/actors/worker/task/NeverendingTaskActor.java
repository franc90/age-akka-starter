package org.age.akka.core.actors.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.start.common.utils.SleepUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class NeverendingTaskActor extends TaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private int counter = 0;

    public NeverendingTaskActor() {
        receive(getDefaultReceiveBuilder()
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    protected void doTask() throws Exception {
        log.info("start doing task");
        while (true) {
            String time = timeFormatter.format(ZonedDateTime.now());
            log.info("{}. attempt to log at {}. Because YOLO", ++counter, time);
            SleepUtils.sleep(1000L);
            checkIfInterrupted();
            if (paused || cancelled) {
                return;
            }
        }
    }
}
