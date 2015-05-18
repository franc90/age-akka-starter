package org.age.akka.configuration;

public class RemoteActor {

    public static final String SEPARATOR = "/";

    private final ActorSystem actorSystem;

    private final AkkaClusterMember host;

    private final RemoteActor parent;

    private final String name;

    public RemoteActor(ActorSystem actorSystem, AkkaClusterMember host, RemoteActor parent, String name) {
        this.actorSystem = actorSystem;
        this.host = host;
        this.parent = parent;
        this.name = name;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    public AkkaClusterMember getHost() {
        return host;
    }

    public RemoteActor getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getActorPath() {
        if (parent != null) {
            return parent.getName() + SEPARATOR + getName();
        }

        return new StringBuilder()
                .append("akka.")
                .append(actorSystem.getProtocol())
                .append("://")
                .append(actorSystem.getName())
                .append("@")
                .append(host.getHost())
                .append(":")
                .append(host.getPort())
                .append(SEPARATOR)
                .append("user")
                .append(SEPARATOR)
                .append(name)
                .toString();
    }

    public static class Builder {

        private ActorSystem actorSystem;
        private AkkaClusterMember host;
        private RemoteActor parent;
        private String name;

        public Builder withActorSystem(ActorSystem actorSystem) {
            this.actorSystem = actorSystem;
            return this;
        }

        public Builder withHost(AkkaClusterMember host) {
            this.host = host;
            return this;
        }

        public Builder withParent(RemoteActor parent) {
            this.parent = parent;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public RemoteActor build() {
            return new RemoteActor(actorSystem, host, parent, name);
        }
    }
}
