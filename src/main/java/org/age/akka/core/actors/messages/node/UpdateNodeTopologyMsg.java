package org.age.akka.core.actors.messages.node;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.custom.NodeId;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.Serializable;

public class UpdateNodeTopologyMsg implements Serializable {

    private final DirectedGraph<NodeId, DefaultEdge> topology;

    public UpdateNodeTopologyMsg(DirectedGraph<NodeId, DefaultEdge> topology) {
        this.topology = topology;
    }

    public DirectedGraph<NodeId, DefaultEdge> getTopology() {
        return topology;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topology", topology)
                .toString();

    }

}
