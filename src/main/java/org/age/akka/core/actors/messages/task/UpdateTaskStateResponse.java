package org.age.akka.core.actors.messages.task;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateTaskStateResponse implements Message {

    public enum State {
        INIT,
        STARTED_OR_RESUMED,
        PAUSED,
        CANCELLED
    }

    private final State state;

    public UpdateTaskStateResponse(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UpdateTaskStateResponse that = (UpdateTaskStateResponse) o;

        return new EqualsBuilder()
                .append(state, that.state)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(state)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("state", state)
                .toString();
    }

}
