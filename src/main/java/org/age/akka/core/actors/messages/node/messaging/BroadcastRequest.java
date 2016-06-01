package org.age.akka.core.actors.messages.node.messaging;

import java.io.Serializable;

public class BroadcastRequest<T extends Serializable> extends SerializableContentMessage<T> {

    public BroadcastRequest(T content) {
        super(content);
    }

}
