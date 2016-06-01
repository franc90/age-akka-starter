package org.age.akka.core.actors.messages.worker.lifecycle;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AddWorkerSucceededResponse implements Message {

    private final NodeId addedActorId;

    public AddWorkerSucceededResponse(NodeId id) {
        this.addedActorId = id;
    }

    public NodeId getAddedActorId() {
        return addedActorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AddWorkerSucceededResponse that = (AddWorkerSucceededResponse) o;

        return new EqualsBuilder()
                .append(addedActorId, that.addedActorId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(addedActorId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("addedActorId", addedActorId)
                .toString();
    }
}
