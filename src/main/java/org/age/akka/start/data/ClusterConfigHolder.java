package org.age.akka.start.data;

import java.io.Serializable;
import java.util.List;

public class ClusterConfigHolder implements Serializable {

    private final AkkaNode currentNode;

    /**
     * Nodes that Akka cluster consists of - knows also as seed nodes
     */
    private final List<AkkaNode> clusterNodes;

    public ClusterConfigHolder(AkkaNode currentNode, List<AkkaNode> clusterNodes) {
        this.currentNode = currentNode;
        this.clusterNodes = clusterNodes;
    }

    public AkkaNode getCurrentNode() {
        return currentNode;
    }

    public List<AkkaNode> getClusterNodes() {
        return clusterNodes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private AkkaNode currentNode;
        private List<AkkaNode> clusterNodes;

        public Builder withCurrentNode(AkkaNode currentNode) {
            this.currentNode = currentNode;
            return this;
        }

        public Builder withClusterNodes(List<AkkaNode> clusterNodes) {
            this.clusterNodes = clusterNodes;
            return this;
        }

        public ClusterConfigHolder build() {
            return new ClusterConfigHolder(currentNode, clusterNodes);
        }
    }
}
