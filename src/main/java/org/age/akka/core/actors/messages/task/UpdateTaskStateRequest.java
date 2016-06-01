package org.age.akka.core.actors.messages.task;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateTaskStateRequest implements Message {

    public enum Type {
        START,
        PAUSE,
        RESUME,
        CANCEL
    }

    private final Type type;

    public UpdateTaskStateRequest(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UpdateTaskStateRequest that = (UpdateTaskStateRequest) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .toString();
    }

}
