package org.age.akka.core.helper;

public class PathCreator {

    private static final String URL_START = "akka.tcp://";

    private static final String USER = "user";

    private static final String SEPARATOR = "/";

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
