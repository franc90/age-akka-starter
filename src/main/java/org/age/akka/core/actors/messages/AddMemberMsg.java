package org.age.akka.core.actors.messages;

import akka.actor.Address;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class AddMemberMsg implements Serializable {

    private final Address actorAddress;

    public AddMemberMsg(Address actorAddress) {
        this.actorAddress = actorAddress;
    }

    public Address getActorAddress() {
        return actorAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AddMemberMsg that = (AddMemberMsg) o;

        return new EqualsBuilder()
                .append(actorAddress, that.actorAddress)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(actorAddress)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("actorAddress", actorAddress)
                .toString();
    }
}
