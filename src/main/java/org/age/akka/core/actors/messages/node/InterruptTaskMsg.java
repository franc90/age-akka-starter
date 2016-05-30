package org.age.akka.core.actors.messages.node;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class InterruptTaskMsg implements Serializable {

    private final boolean pause;

    private final boolean cancel;

    public InterruptTaskMsg(boolean pause, boolean cancel) {
        this.pause = pause;
        this.cancel = cancel;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isCancel() {
        return cancel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterruptTaskMsg that = (InterruptTaskMsg) o;

        return new EqualsBuilder()
                .append(pause, that.pause)
                .append(cancel, that.cancel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pause)
                .append(cancel)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pause", pause)
                .add("cancel", cancel)
                .toString();
    }
}
