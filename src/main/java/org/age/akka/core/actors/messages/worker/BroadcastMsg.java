package org.age.akka.core.actors.messages.worker;

import java.io.Serializable;

public interface BroadcastMsg<T extends Serializable> extends Serializable {

    T getContent();

}
