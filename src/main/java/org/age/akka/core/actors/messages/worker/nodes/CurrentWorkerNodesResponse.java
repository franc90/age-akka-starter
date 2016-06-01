package org.age.akka.core.actors.messages.worker.nodes;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class CurrentWorkerNodesResponse implements Message {

    private final Set<NodeId> nodeIdSet;

    public CurrentWorkerNodesResponse(Set<NodeId> nodeIdSet) {
        this.nodeIdSet = nodeIdSet;
    }

    public Set<NodeId> getNodeIdSet() {
        return nodeIdSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CurrentWorkerNodesResponse that = (CurrentWorkerNodesResponse) o;

        return new EqualsBuilder()
                .append(nodeIdSet, that.nodeIdSet)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(nodeIdSet)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeIdSet", nodeIdSet)
                .toString();
    }
}
