package org.age.akka.core.messages.node.messaging;

import java.io.Serializable;

public class TaskMessage<T extends Serializable> extends SerializableContentMessage<T> {

    public TaskMessage(T content) {
        super(content);
    }

}
