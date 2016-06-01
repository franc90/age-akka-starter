package org.age.akka.core.messages.task;

import org.age.akka.core.messages.SingleValueMessage;

public class UpdateTaskStateRequest extends SingleValueMessage<UpdateTaskStateRequest.Type> {

    public enum Type {
        START,
        PAUSE,
        RESUME,
        CANCEL
    }

    public UpdateTaskStateRequest(Type type) {
        super(type);
    }

    public Type getType() {
        return getValue();
    }

}
