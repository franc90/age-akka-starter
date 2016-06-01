package org.age.akka.core.actors.messages.lifecycle;

import akka.actor.Address;
import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LifecycleUpdatedRequest implements Message {

    private final Type type;

    private final Address address;

    public LifecycleUpdatedRequest(Type type, Address address) {
        this.type = type;
        this.address = address;
    }

    public Type getType() {
        return type;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        LifecycleUpdatedRequest that = (LifecycleUpdatedRequest) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .append(address, that.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(address)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("address", address)
                .toString();
    }

    public enum Type {
        ADD, REMOVE
    }

}
