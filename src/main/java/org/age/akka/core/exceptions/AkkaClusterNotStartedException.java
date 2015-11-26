package org.age.akka.core.exceptions;

public class AkkaClusterNotStartedException extends RuntimeException {

    public AkkaClusterNotStartedException(String message) {
        super(message);
    }

}
