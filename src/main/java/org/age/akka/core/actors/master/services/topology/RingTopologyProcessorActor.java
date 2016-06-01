package org.age.akka.core.actors.master.services.topology;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.core.actors.worker.NodeId;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableDirectedGraph;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.getLast;

public class RingTopologyProcessorActor extends AbstractTopologyProcessorActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public RingTopologyProcessorActor() {
        super();
    }

    @Override
    protected UnmodifiableDirectedGraph<NodeId, DefaultEdge> createNewTopologyWithNodes(Set<NodeId> nodeIds) {
        log.debug("process new topology with nodes {}", nodeIds.size());

        DefaultDirectedGraph<NodeId, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodeIds.forEach(graph::addVertex);

        List<NodeId> sortedIds = nodeIds.stream()
                .sorted()
                .collect(Collectors.toList());

        sortedIds.stream().reduce(getLast(sortedIds), (nodeIdentity1, nodeIdentity2) -> {
            graph.addEdge(nodeIdentity1, nodeIdentity2);
            return nodeIdentity2;
        });

        return new UnmodifiableDirectedGraph<>(graph);
    }

}
