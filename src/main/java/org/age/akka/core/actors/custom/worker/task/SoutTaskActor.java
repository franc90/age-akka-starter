package org.age.akka.core.actors.custom.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.start.common.utils.SleepUtils;

public class SoutTaskActor extends TaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private int counter = 0;

    public SoutTaskActor() {
        receive(getDefaultReceiveBuilder()
                .build());
    }

    protected void doTask() throws Exception {
        while (true) {
            System.out.println(++counter + ". attempt to write to sout");
            SleepUtils.sleep(1000L);
            checkIfInterrupted();
            if (paused || cancelled) {
                return;
            }
        }
    }
}
