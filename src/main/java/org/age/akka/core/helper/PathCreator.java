package org.age.akka.core.helper;

public class PathCreator {

    public static final String URL_START = "akka.tcp://";

    public static final String USER = "user";

    public static final String SEPARATOR = "/";

    public static String createPath(String hostPort, String... paths) {
        StringBuilder builtPath = new StringBuilder()
                .append(URL_START)
                .append(hostPort)
                .append(SEPARATOR)
                .append(USER);

        for (String path : paths) {
            builtPath
                    .append(SEPARATOR)
                    .append(path);
        }

        return builtPath.toString();
    }

}
