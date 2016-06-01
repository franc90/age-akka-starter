package org.age.akka.core.messages.topology;

import org.age.akka.core.actors.worker.NodeId;
import org.age.akka.core.messages.SingleValueMessage;

import java.util.HashSet;

public class NewTopologyRequest extends SingleValueMessage<HashSet<NodeId>> {

    public NewTopologyRequest(HashSet<NodeId> nodeIds) {
        super(nodeIds);
    }

    public HashSet<NodeId> getNodeIds() {
        return getValue();
    }

}
