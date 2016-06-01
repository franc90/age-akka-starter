package org.age.akka.core.actors.messages.node.lifecycle;

import com.google.common.base.MoreObjects;
import org.age.akka.core.actors.messages.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TaskInterruptedResponse implements Message {

    private final boolean paused;

    private final boolean cancelled;

    public TaskInterruptedResponse(boolean paused, boolean cancelled) {
        this.paused = paused;
        this.cancelled = cancelled;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TaskInterruptedResponse that = (TaskInterruptedResponse) o;

        return new EqualsBuilder()
                .append(paused, that.paused)
                .append(cancelled, that.cancelled)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(paused)
                .append(cancelled)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("paused", paused)
                .add("cancelled", cancelled)
                .toString();
    }
}