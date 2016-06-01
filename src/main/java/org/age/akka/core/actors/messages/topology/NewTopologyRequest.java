package org.age.akka.core.actors.messages.topology;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class NewTopologyRequest implements Message {

    private final Set<NodeId> nodeIds;

    public NewTopologyRequest(Set<NodeId> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public Set<NodeId> getNodeIds() {
        return nodeIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NewTopologyRequest that = (NewTopologyRequest) o;

        return new EqualsBuilder()
                .append(nodeIds, that.nodeIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(nodeIds)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeIds", nodeIds)
                .toString();
    }
}
