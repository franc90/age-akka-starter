package org.age.akka.core.messages.node.messaging;

import org.age.akka.core.messages.Message;

import java.io.Serializable;

public interface WorkerMessage<T extends Serializable> extends Message {

    T getContent();

}
