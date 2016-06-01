package org.age.akka.core.actors.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SimpleTaskActor extends TaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private boolean finished;

    public SimpleTaskActor() {
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
        finished = true;
    }
}
