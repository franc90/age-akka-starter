package org.age.akka.core.actors.custom;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class TaskActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    boolean isRunning;

    public TaskActor() {
        log.info("Create task actor");
        receive(ReceiveBuilder
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }
}
