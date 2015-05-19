package org.age.akka.structures;

import java.util.Collection;

public class AkkaNodeConfig {

    private final AkkaNode currentNode;

    /**
     * Nodes that Akka cluster consists of
     */
    private final Collection<AkkaNode> seedNodes;

    public AkkaNodeConfig(AkkaNode currentNode, Collection<AkkaNode> seedNodes) {
        this.currentNode = currentNode;
        this.seedNodes = seedNodes;
    }

    public AkkaNode getCurrentNode() {
        return currentNode;
    }

    public Collection<AkkaNode> getSeedNodes() {
        return seedNodes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private AkkaNode currentNode;
        private Collection<AkkaNode> seedNodes;

        public Builder withCurrentNode(AkkaNode currentNode) {
            this.currentNode = currentNode;
            return this;
        }

        public Builder withSeedNodes(Collection<AkkaNode> seedNodes) {
            this.seedNodes = seedNodes;
            return this;
        }

        public AkkaNodeConfig build() {
            return new AkkaNodeConfig(currentNode, seedNodes);
        }
    }
}
