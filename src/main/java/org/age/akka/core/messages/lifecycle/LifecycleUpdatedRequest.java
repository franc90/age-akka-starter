package org.age.akka.core.messages.lifecycle;

import akka.actor.Address;
import org.age.akka.core.messages.DoubleValueMessage;

public class LifecycleUpdatedRequest extends DoubleValueMessage<LifecycleUpdatedRequest.Type, Address> {

    public enum Type {
        ADD, REMOVE
    }

    public LifecycleUpdatedRequest(Type type, Address address) {
        super(type, address);
    }

    public Type getType() {
        return getFirstValue();
    }

    public Address getAddress() {
        return getSecondValue();
    }

}
