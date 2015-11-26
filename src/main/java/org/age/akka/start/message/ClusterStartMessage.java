package org.age.akka.start.message;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class ClusterStartMessage implements Serializable {

    private final String senderId;

    private final ClusterStartMessageType clusterStartMessageType;

    private ClusterStartMessage(ClusterStartMessageBuilder builder) {
        this.senderId = builder.senderId;
        this.clusterStartMessageType = builder.clusterStartMessageType;
    }

    public String getSenderId() {
        return senderId;
    }

    public ClusterStartMessageType getClusterStartMessageType() {
        return clusterStartMessageType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("senderId", senderId)
                .add("clusterStartMessageType", clusterStartMessageType)
                .toString();
    }

    public static ClusterStartMessageBuilder builder() {
        return new ClusterStartMessageBuilder();
    }

    public static class ClusterStartMessageBuilder {
        private String senderId;
        private ClusterStartMessageType clusterStartMessageType;


        public ClusterStartMessageBuilder withSenderId(String senderId) {
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
}
