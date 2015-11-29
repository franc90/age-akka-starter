package org.age.akka.start.common.message;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.age.akka.start.common.data.NodeId;

import java.io.Serializable;

public class ClusterStartMessage implements Serializable {

    private final NodeId senderId;

    private final ClusterStartMessageType clusterStartMessageType;

    private ClusterStartMessage(ClusterStartMessageBuilder builder) {
        this.senderId = builder.senderId;
        this.clusterStartMessageType = builder.clusterStartMessageType;
    }

    public NodeId getSenderId() {
        return senderId;
    }

    public ClusterStartMessageType getClusterStartMessageType() {
        return clusterStartMessageType;
    }

    public static ClusterStartMessageBuilder builder() {
        return new ClusterStartMessageBuilder();
    }

    public static class ClusterStartMessageBuilder {

        private NodeId senderId;
        private ClusterStartMessageType clusterStartMessageType;

        public ClusterStartMessageBuilder withSenderId(NodeId senderId) {
            this.senderId = senderId;
            return this;
        }


        public ClusterStartMessageBuilder withClusterStartMessageType(ClusterStartMessageType clusterStartMessageType) {
            this.clusterStartMessageType = clusterStartMessageType;
            return this;
        }

        public ClusterStartMessage build() {
            return new ClusterStartMessage(this);
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterStartMessage that = (ClusterStartMessage) o;
        return Objects.equal(senderId, that.senderId) &&
                clusterStartMessageType == that.clusterStartMessageType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(senderId, clusterStartMessageType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("senderId", senderId)
                .add("clusterStartMessageType", clusterStartMessageType)
                .toString();
    }
}
