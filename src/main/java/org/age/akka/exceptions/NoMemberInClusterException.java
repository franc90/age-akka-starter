package org.age.akka.exceptions;

public class NoMemberInClusterException extends RuntimeException {

    public NoMemberInClusterException(String message) {
        super(message);
    }

}
