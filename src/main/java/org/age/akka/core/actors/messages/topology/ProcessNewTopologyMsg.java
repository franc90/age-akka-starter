package org.age.akka.core.actors.messages.topology;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.custom.worker.NodeId;

import java.io.Serializable;
import java.util.Set;

public class ProcessNewTopologyMsg implements Serializable {

    private final Set<NodeId> nodeIds;

    public ProcessNewTopologyMsg(Set<NodeId> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public Set<NodeId> getNodeIds() {
        return nodeIds;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("nodeIds", nodeIds)
                .toString();
    }
}
