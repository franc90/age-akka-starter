package org.age.akka.core.actors.messages.task;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class TaskStateMsg implements Serializable {

    private final Type type;

    public TaskStateMsg(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .toString();
    }

    public enum Type {
        START,
        PAUSE,
        RESUME,
        CANCEL
    }

}
