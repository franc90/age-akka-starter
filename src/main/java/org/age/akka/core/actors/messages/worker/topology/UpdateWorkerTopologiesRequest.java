package org.age.akka.core.actors.messages.worker.topology;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class UpdateWorkerTopologiesRequest implements Message {

    private final DirectedGraph<NodeId, DefaultEdge> topology;

    public UpdateWorkerTopologiesRequest(DirectedGraph<NodeId, DefaultEdge> topology) {
        this.topology = topology;
    }

    public DirectedGraph<NodeId, DefaultEdge> getTopology() {
        return topology;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UpdateWorkerTopologiesRequest that = (UpdateWorkerTopologiesRequest) o;

        return new EqualsBuilder()
                .append(topology, that.topology)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(topology)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topology", topology)
                .toString();
    }
}
