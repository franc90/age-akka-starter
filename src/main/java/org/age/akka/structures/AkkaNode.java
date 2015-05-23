package org.age.akka.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AkkaNode implements Serializable {

    private final String actorSystemName;

    /**
     * host name/ip - therefore should be something other than 0.0.0.0, localhost or NAT'ed IP
     */
    private final String hostname;
    private final int port;
    private final String nodeId = UUID.randomUUID().toString();
    private final List<String> roles;

    private AkkaNode(String actorSystemName, String hostname, int port, List<String> roles) {
        this.actorSystemName = actorSystemName;
        this.hostname = hostname;
        this.port = port;
        this.roles = roles;
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

    public List<String> getRoles() {
        return roles;
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
        private List<String> roles = new ArrayList<>();

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

        public Builder withRoles(List<String> roles) {
            this.roles.clear();
            this.roles.addAll(roles);
            return this;
        }

        public Builder addRole(String role) {
            this.roles.add(role);
            return this;
        }

        public AkkaNode build() {
            return new AkkaNode(actorSystemName, hostname, port, roles);
        }
    }

}
