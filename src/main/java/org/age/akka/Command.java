package org.age.akka;

import java.io.Serializable;

public class Command implements Serializable {

    public enum Type {NEW, LIST}

    private final Type type;
    private final String param;

    public Command(Type type, String param) {
        this.type = type;
        this.param = param;
    }

    public Type getType() {
        return type;
    }

    public String getParam() {
        return param;
    }
}
