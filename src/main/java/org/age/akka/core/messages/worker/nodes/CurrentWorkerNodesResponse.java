package org.age.akka.core.messages.worker.nodes;

import org.age.akka.core.actors.worker.NodeId;
import org.age.akka.core.messages.SingleValueMessage;

import java.util.HashSet;

public class CurrentWorkerNodesResponse extends SingleValueMessage<HashSet<NodeId>> {

    public CurrentWorkerNodesResponse(HashSet<NodeId> nodeIdSet) {
        super(nodeIdSet);
    }

    public HashSet<NodeId> getNodeIdSet() {
        return getValue();
    }

}
