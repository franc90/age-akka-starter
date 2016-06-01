package org.age.akka.core.messages.node.messaging;

import org.age.akka.core.messages.SingleValueMessage;

import java.io.Serializable;

public abstract class SerializableContentMessage<T extends Serializable> extends SingleValueMessage<T> implements WorkerMessage<T> {

    public SerializableContentMessage(T content) {
        super(content);
    }

    @Override
    public T getContent() {
        return getValue();
    }

}
