package org.age.akka.configuration;

public class AkkaClusterMember {

    private final String host;

    private final int port;

    private final String uid;

    public AkkaClusterMember(String host, int port, String uid) {
        this.host = host;
        this.port = port;
        this.uid = uid;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUid() {
        return uid;
    }

    public static class Builder {
        private String host;
        private int port;
        private String uid;

        public Builder withHost(String host) {
            this.host = host;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withUuid(String uid) {
            this.uid = uid;
            return this;
        }

        public AkkaClusterMember build() {
            return new AkkaClusterMember(host, port, uid);
        }

    }
}
