package org.age.akka.structures;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class AkkaNodeConfig implements Serializable {

    private final AkkaNode currentNode;

    /**
     * Nodes that Akka cluster consists of
     */
    private final List<AkkaNode> seedNodes;

    public AkkaNodeConfig(AkkaNode currentNode, List<AkkaNode> seedNodes) {
        this.currentNode = currentNode;
        this.seedNodes = seedNodes;
    }

    public AkkaNode getCurrentNode() {
        return currentNode;
    }

    public List<AkkaNode> getSeedNodes() {
        return seedNodes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private AkkaNode currentNode;
        private List<AkkaNode> seedNodes;

        public Builder withCurrentNode(AkkaNode currentNode) {
            this.currentNode = currentNode;
            return this;
        }

        public Builder withSeedNodes(List<AkkaNode> seedNodes) {
            this.seedNodes = seedNodes;
            return this;
        }

        public AkkaNodeConfig build() {
            return new AkkaNodeConfig(currentNode, seedNodes);
        }
    }
}
