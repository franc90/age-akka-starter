package org.age.akka.start.initialization;

import org.springframework.util.SocketUtils;

import javax.inject.Named;

@Named
public class PortFinder {

    private int MIN_PORT = 2556;

    private int MAX_PORT = 20000;

    public int getPort() {
        return SocketUtils.findAvailableTcpPort(MIN_PORT, MAX_PORT);
    }

}
