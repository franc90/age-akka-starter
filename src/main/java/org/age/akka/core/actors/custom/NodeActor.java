package org.age.akka.core.actors.custom;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.messages.HelloMessage;
import org.age.akka.core.actors.messages.node.UpdateNodeTopologyMsg;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class NodeActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private DirectedGraph<NodeId, DefaultEdge> topology;

    public NodeActor() {
        log.info("spawn " + self().path());

        receive(ReceiveBuilder
                .match(HelloMessage.class, this::helloMessage)
                .match(UpdateNodeTopologyMsg.class, this::updateTopology)
                .matchAny((m -> log.warning("unexpected message " + m)))
                .build());
    }

    private void updateTopology(UpdateNodeTopologyMsg msg) {
        topology = msg.getTopology();
        log.info("Node received updated topology: " + topology);
    }

    private void helloMessage(HelloMessage msg) {
        System.out.println("Received hello message " + msg.getContent());
    }
}
