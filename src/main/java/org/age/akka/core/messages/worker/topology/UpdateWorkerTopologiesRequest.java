package org.age.akka.core.messages.worker.topology;

import org.age.akka.core.actors.worker.NodeId;
import org.age.akka.core.messages.SingleValueMessage;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableDirectedGraph;

public class UpdateWorkerTopologiesRequest extends SingleValueMessage<UnmodifiableDirectedGraph<NodeId, DefaultEdge>> {

    public UpdateWorkerTopologiesRequest(UnmodifiableDirectedGraph<NodeId, DefaultEdge> topology) {
        super(topology);
    }

    public UnmodifiableDirectedGraph<NodeId, DefaultEdge> getTopology() {
        return getValue();
    }

}
