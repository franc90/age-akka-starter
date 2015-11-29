package org.age.akka.start.common.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.inject.Named;
import java.io.Serializable;
import java.util.UUID;

@Named
public class NodeId implements Serializable {

    private final UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeId nodeId = (NodeId) o;
        return Objects.equal(id, nodeId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .toString();
    }
}
