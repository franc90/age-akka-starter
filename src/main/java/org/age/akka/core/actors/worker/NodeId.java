package org.age.akka.core.actors.worker;

import akka.actor.Address;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

import static scala.compat.java8.JFunction.func;

public class NodeId implements Serializable, Comparable<NodeId> {

    private final String hostname;

    private final int port;

    public NodeId(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return "node_" + hostname + "_" + port;
    }

    public static NodeId fromAddress(Address address) {
        String host = address.host().getOrElse(func(() -> ""));
        int port = address.port().getOrElse(func(() -> 0));

        return new NodeId(host, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NodeId nodeId = (NodeId) o;

        return new EqualsBuilder()
                .append(hostname, nodeId.hostname)
                .append(port, nodeId.port)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hostname)
                .append(port)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("hostname", hostname)
                .add("port", port)
                .toString();
    }

    @Override
    public int compareTo(NodeId id) {
        if (this == id) {
            return 0;
        }

        int result = this.hostname.compareTo(id.hostname);
        if (result != 0) {
            return result;
        }

        if (this.port < id.port) {
            return -1;
        }
        if (this.port > id.port) {
            return 1;
        }
        return 0;
    }
}
