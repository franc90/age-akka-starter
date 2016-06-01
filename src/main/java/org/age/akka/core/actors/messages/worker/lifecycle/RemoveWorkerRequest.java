package org.age.akka.core.actors.messages.worker.lifecycle;

import akka.actor.Address;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RemoveWorkerRequest implements UpdateWorkersRequest {

    private final Address workerAddress;

    public RemoveWorkerRequest(Address workerAddress) {
        this.workerAddress = workerAddress;
    }

    public Address getWorkerAddress() {
        return workerAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RemoveWorkerRequest that = (RemoveWorkerRequest) o;

        return new EqualsBuilder()
                .append(workerAddress, that.workerAddress)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(workerAddress)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("workerAddress", workerAddress)
                .toString();
    }
}
