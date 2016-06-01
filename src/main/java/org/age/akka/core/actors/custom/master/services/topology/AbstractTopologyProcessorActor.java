package org.age.akka.core.actors.custom.master.services.topology;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.topology.NewTopologyResponse;
import org.age.akka.core.actors.messages.topology.NewTopologyRequest;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

public abstract class AbstractTopologyProcessorActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    public AbstractTopologyProcessorActor() {
        receive(ReceiveBuilder
                .match(NewTopologyRequest.class, this::processNewTopologyRequest)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    private void processNewTopologyRequest(NewTopologyRequest request) {
        Set<NodeId> nodeIds = request.getNodeIds();

        DirectedGraph<NodeId, DefaultEdge> topology = createNewTopologyWithNodes(nodeIds);
        log.info("new topology Graph: {}", topology);
        sender().tell(new NewTopologyResponse(topology), self());
    }

    protected abstract DirectedGraph<NodeId, DefaultEdge> createNewTopologyWithNodes(Set<NodeId> nodeIds);

}
