package org.age.akka.core.messages.task;

import org.age.akka.core.messages.SingleValueMessage;

public class UpdateTaskStateResponse extends SingleValueMessage<UpdateTaskStateResponse.State> {

    public enum State {
        INIT,
        STARTED_OR_RESUMED,
        PAUSED,
        CANCELLED
    }

    public UpdateTaskStateResponse(State state) {
        super(state);
    }

    public State getState() {
        return getValue();
    }

}
