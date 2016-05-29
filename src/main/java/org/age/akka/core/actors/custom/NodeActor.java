package org.age.akka.core.actors.custom;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.messages.HelloMessage;

import java.util.HashMap;
import java.util.Map;

public class NodeActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public NodeActor() {
        log.info("spawn " + self().path());

        receive(ReceiveBuilder
                .match(HelloMessage.class, this::helloMessage)
                .matchAny((m -> log.warning("unexpected message " + m)))
                .build());
    }

    private void helloMessage(HelloMessage msg) {
        System.out.println("Received hello message " + msg.getContent());
    }
}
