package org.age.akka.start.common.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class AkkaNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ActorSystemName actorSystemName;

    /**
     * host name/ip - therefore should be something other than 0.0.0.0, localhost or NAT'ed IP
     */
    private final Hostname hostname;

    private final Port port;

    private final List<Role> roles;

    public AkkaNode(Builder builder) {
        this.actorSystemName = builder.actorSystemName;
        this.hostname = builder.hostname;
        this.port = builder.port;
        this.roles = builder.roles;
    }

    public ActorSystemName getActorSystemName() {
        return actorSystemName;
    }

    public Hostname getHostname() {
        return hostname;
    }

    public Port getPort() {
        return port;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {


        private ActorSystemName actorSystemName;
        private Hostname hostname;
        private Port port;
        private List<Role> roles = new LinkedList<>();

        public Builder withActorSystemName(ActorSystemName actorSystemName) {
            this.actorSystemName = actorSystemName;
            return this;
        }

        public Builder withHostname(Hostname hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder withPort(Port port) {
            this.port = port;
            return this;
        }

        public Builder withRoles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder addRole(Role role) {
            this.roles.add(role);
            return this;
        }

        public AkkaNode build() {
            AkkaNode akkaNode = new AkkaNode(this);
            return akkaNode;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AkkaNode akkaNode = (AkkaNode) o;
        return Objects.equal(actorSystemName, akkaNode.actorSystemName) &&
                Objects.equal(hostname, akkaNode.hostname) &&
                Objects.equal(port, akkaNode.port) &&
                Objects.equal(roles, akkaNode.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(actorSystemName, hostname, port, roles);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("actorSystemName", actorSystemName)
                .add("hostname", hostname)
                .add("port", port)
                .add("roles", roles)
                .toString();
    }
}
