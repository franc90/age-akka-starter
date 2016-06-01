package org.age.akka.core.actors.master.services.topology;

import org.age.akka.core.actors.worker.NodeId;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableDirectedGraph;

import java.util.Set;

import static com.google.common.collect.Sets.cartesianProduct;

public class FullyConnectedTopologyProcessorActor extends AbstractTopologyProcessorActor {

    public FullyConnectedTopologyProcessorActor() {
        super();
    }

    @Override
    protected UnmodifiableDirectedGraph<NodeId, DefaultEdge> createNewTopologyWithNodes(Set<NodeId> nodeIds) {
        final DefaultDirectedGraph<NodeId, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodeIds.forEach(graph::addVertex);

        cartesianProduct(nodeIds, nodeIds).forEach(elem -> {
            NodeId id1 = elem.get(0);
            NodeId id2 = elem.get(1);
            if (!id1.equals(id2)) {
                graph.addEdge(id1, id2);
            }
        });

        return new UnmodifiableDirectedGraph<>(graph);
    }
}
