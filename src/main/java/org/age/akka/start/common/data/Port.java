package org.age.akka.start.common.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.springframework.util.SocketUtils;

public class Port {

    private static final int MIN_PORT = 2556;

    private static final int MAX_PORT = 20000;

    private final int port;

    public Port() {
        this.port = findAvailablePort();
    }

    public Port(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String stringValue() {
        return String.valueOf(port);
    }

    public int findAvailablePort() {
        return SocketUtils.findAvailableTcpPort(MIN_PORT, MAX_PORT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Port port1 = (Port) o;
        return port == port1.port;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(port);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("port", port)
                .toString();
    }
}
