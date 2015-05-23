package org.age.akka.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.messages.ListNodes;
import org.age.akka.messages.RegisterWorker;

import java.util.LinkedList;
import java.util.List;

public class ClusterManagingActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final List<ActorRef> workers = new LinkedList<>();

    public ClusterManagingActor() {
        receive(ReceiveBuilder
                .match(RegisterWorker.class, e -> {
                    ActorRef sender = sender();
                    log.info("Adding sender: "+ sender);
                    workers.add(sender);
                    sender.tell(new ListNodes(), self());
                })
                .match(ListNodes.class, e-> {
                    log.info("Listing workers: ");
                    workers.stream().map(ActorRef::path).map(ActorPath::toSerializationFormat).forEach(log::info);
                })
                .matchAny(e -> log.info("GOT: " + e))
                .build());
    }
}

