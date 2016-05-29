package org.age.akka.core.actors.messages.worker;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.custom.NodeId;

import java.io.Serializable;
import java.util.Set;

public class NodesMsg implements Serializable {

    private final Set<NodeId> nodeIdSet;

    public NodesMsg(Set<NodeId> nodeIdSet) {
        this.nodeIdSet = nodeIdSet;
    }

    public Set<NodeId> getNodeIdSet() {
        return nodeIdSet;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeIdSet", nodeIdSet)
                .toString();
    }
}
