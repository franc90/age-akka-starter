package org.age.akka.start.initialization;

import javax.inject.Named;
import java.net.*;
import java.util.Enumeration;

@Named
public class HostnameFinder {

    public String getHostname() throws UnknownHostException {
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
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (SocketException e) {
            return Inet4Address.getLocalHost().getHostAddress();
        }
    }

}
