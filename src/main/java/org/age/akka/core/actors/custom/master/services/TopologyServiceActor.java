package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class TopologyServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    public TopologyServiceActor() {
        receive(ReceiveBuilder
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }
}
