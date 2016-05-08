package org.age.akka.start.common.message;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class ClusterStartMessage implements Serializable {

    private final String senderUUID;

    private final ClusterStartMessageType clusterStartMessageType;

    private ClusterStartMessage(ClusterStartMessageBuilder builder) {
        this.senderUUID = builder.senderUUID;
        this.clusterStartMessageType = builder.clusterStartMessageType;
    }

    public String getSenderUUID() {
        return senderUUID;
    }

    public ClusterStartMessageType getClusterStartMessageType() {
        return clusterStartMessageType;
    }

    public static ClusterStartMessageBuilder builder() {
        return new ClusterStartMessageBuilder();
    }

    public static class ClusterStartMessageBuilder {

        private String senderUUID;
        private ClusterStartMessageType clusterStartMessageType;

        public ClusterStartMessageBuilder withSenderUUID(String senderUUID) {
            this.senderUUID = senderUUID;
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
        return Objects.equal(senderUUID, that.senderUUID) &&
                clusterStartMessageType == that.clusterStartMessageType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(senderUUID, clusterStartMessageType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("senderUUID", senderUUID)
                .add("clusterStartMessageType", clusterStartMessageType)
                .toString();
    }
}
