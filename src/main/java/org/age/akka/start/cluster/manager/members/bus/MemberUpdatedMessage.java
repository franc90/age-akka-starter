package org.age.akka.start.cluster.manager.members.bus;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class MemberUpdatedMessage {

    private final State state;

    private final String uuid;

    public MemberUpdatedMessage(State state, String uuid) {
        this.state = state;
        this.uuid = uuid;
    }

    public State getState() {
        return state;
    }

    public String getUuid() {
        return uuid;
    }

    public enum State {
        ADDED, REMOVED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberUpdatedMessage that = (MemberUpdatedMessage) o;
        return state == that.state &&
                Objects.equal(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(state, uuid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("state", state)
                .add("uuid", uuid)
                .toString();
    }
}
