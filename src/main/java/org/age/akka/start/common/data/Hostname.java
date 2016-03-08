package org.age.akka.start.common.data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.*;
import java.util.Enumeration;

public class Hostname implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Hostname.class);

    private final String hostname;

    public Hostname() {
        hostname = findHostname();
    }

    public Hostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

    private String findHostname() {
        try {
            return findInetHostname();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    private String findInetHostname() throws UnknownHostException {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (!networkInterface.isLoopback()) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address.isLoopbackAddress()) {
                            continue;
                        }
                        if (address instanceof Inet4Address) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            logger.debug("Caught SocketException while looking for hostname", e);
        }
        return Inet4Address.getLocalHost().getHostAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hostname hostname1 = (Hostname) o;
        return Objects.equal(hostname, hostname1.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hostname);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("hostname", hostname)
                .toString();
    }
}
