package org.age.akka.core.actors.messages.node.messaging;

import org.age.akka.core.actors.messages.Message;

import java.io.Serializable;

public interface WorkerMessage<T extends Serializable> extends Message {

    T getContent();

}
