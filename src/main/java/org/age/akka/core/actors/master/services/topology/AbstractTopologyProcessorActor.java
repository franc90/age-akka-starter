package org.age.akka.core.actors.master.services.topology;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.worker.NodeId;
import org.age.akka.core.messages.topology.NewTopologyRequest;
import org.age.akka.core.messages.topology.NewTopologyResponse;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableDirectedGraph;

import java.util.Set;

public abstract class AbstractTopologyProcessorActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public AbstractTopologyProcessorActor() {
        receive(ReceiveBuilder
                .match(NewTopologyRequest.class, this::processNewTopologyRequest)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    private void processNewTopologyRequest(NewTopologyRequest request) {
        Set<NodeId> nodeIds = request.getNodeIds();

        UnmodifiableDirectedGraph<NodeId, DefaultEdge> topology = createNewTopologyWithNodes(nodeIds);
        log.debug("new topology Graph: {}", topology);
        sender().tell(new NewTopologyResponse(topology), self());
    }

    protected abstract UnmodifiableDirectedGraph<NodeId, DefaultEdge> createNewTopologyWithNodes(Set<NodeId> nodeIds);

}
