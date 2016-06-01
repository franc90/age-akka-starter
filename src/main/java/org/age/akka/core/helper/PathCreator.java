package org.age.akka.core.helper;

public class PathCreator {

    public static String createPath(String hostname, int port, String actorName) {
        return new StringBuilder()
                .append("akka.tcp://age3@")
                .append(hostname)
                .append(":")
                .append(port)
                .append("/user/")
                .append(actorName)
                .toString();

    }

}
