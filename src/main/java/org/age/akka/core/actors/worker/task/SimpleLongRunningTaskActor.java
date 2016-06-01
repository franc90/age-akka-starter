package org.age.akka.core.actors.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.start.common.utils.SleepUtils;

public class SimpleLongRunningTaskActor extends TaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private boolean finished;

    private int counter;

    public SimpleLongRunningTaskActor() {
        receive(getDefaultReceiveBuilder()
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    protected void doTask() throws Exception {
        if (finished) {
            return;
        }

        log.info("This is the simplest possible example of a computation.");
        for (; counter < 100; counter++) {
            log.info("Iteration {}.", counter);

            SleepUtils.sleep(1000L);
            checkIfInterrupted();
            if (paused || cancelled) {
                return;
            }
        }

        finished = true;
    }
}
