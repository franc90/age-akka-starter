package org.age.akka.core.messages.worker.lifecycle;

import org.age.akka.core.actors.worker.NodeId;
import org.age.akka.core.messages.SingleValueMessage;

public class AddWorkerSucceededResponse extends SingleValueMessage<NodeId> {

    public AddWorkerSucceededResponse(NodeId id) {
        super(id);
    }

    public NodeId getAddedActorId() {
        return getValue();
    }

}
