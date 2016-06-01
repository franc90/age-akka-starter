package org.age.akka.core.messages.worker.lifecycle;

import org.age.akka.core.actors.worker.NodeId;
import org.age.akka.core.messages.SingleValueMessage;

public class AddWorkerFailedResponse extends SingleValueMessage<NodeId> {

    public AddWorkerFailedResponse(NodeId nodeId) {
        super(nodeId);
    }

    public NodeId getFailedWorkerId() {
        return getValue();
    }

}
