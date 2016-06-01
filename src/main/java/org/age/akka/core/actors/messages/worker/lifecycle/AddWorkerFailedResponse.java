package org.age.akka.core.actors.messages.worker.lifecycle;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AddWorkerFailedResponse implements Message {

    private final NodeId actorId;

    public AddWorkerFailedResponse(NodeId nodeId) {
        this.actorId = nodeId;
    }

    public NodeId getActorId() {
        return actorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AddWorkerFailedResponse that = (AddWorkerFailedResponse) o;

        return new EqualsBuilder()
                .append(actorId, that.actorId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(actorId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("actorId", actorId)
                .toString();
    }
}
