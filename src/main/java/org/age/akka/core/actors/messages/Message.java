package org.age.akka.core.actors.messages;

import java.io.Serializable;

public interface Message<T> extends Serializable {

    T getContent();

}
