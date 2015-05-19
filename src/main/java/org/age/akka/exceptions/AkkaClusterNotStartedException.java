package org.age.akka.exceptions;

public class AkkaClusterNotStartedException extends RuntimeException {

    public AkkaClusterNotStartedException(String message) {
        super(message);
    }

}
