package org.age.akka.configuration;

public class ActorSystem {

    private final String name;

    private final String protocol;

    public ActorSystem(String name, String protocol) {
        this.name = name;
        this.protocol = protocol;
    }

    public String getName() {
        return name;
    }

    public String getProtocol() {
        return protocol;
    }

    public static class Builder {

        private String name;
        private String protocol;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public ActorSystem build() {
            return new ActorSystem(name, protocol);
        }
    }

}
