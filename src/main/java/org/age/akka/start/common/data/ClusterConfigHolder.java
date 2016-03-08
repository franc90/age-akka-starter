package org.age.akka.start.common.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.List;

public class ClusterConfigHolder implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClusterConfigHolder that = (ClusterConfigHolder) o;
        return Objects.equal(currentNode, that.currentNode) &&
                Objects.equal(clusterNodes, that.clusterNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currentNode, clusterNodes);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("currentNode", currentNode)
                .add("clusterNodes", clusterNodes)
                .toString();
    }
}
