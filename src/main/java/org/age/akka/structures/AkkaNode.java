package org.age.akka.structures;

import java.util.UUID;

public class AkkaNode {

    private final String actorSystemName;
    private final String hostname;
    private final int port;
    private final String nodeId = UUID.randomUUID().toString();

    private AkkaNode(String actorSystemName, String hostname, int port) {
        this.actorSystemName = actorSystemName;
        this.hostname = hostname;
        this.port = port;
    }

    public String getActorSystemName() {
        return actorSystemName;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getNodeId() {
        return nodeId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "AkkaNode{" +
                "actorSystemName='" + actorSystemName + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }

    public static class Builder {

        private String actorSystemName;
        private String hostname;
        private int port;

        public Builder withActorSystemName(String actorSystemName) {
            this.actorSystemName = actorSystemName;
            return this;
        }

        public Builder withHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public AkkaNode build() {
            return new AkkaNode(actorSystemName, hostname, port);
        }
    }

}
