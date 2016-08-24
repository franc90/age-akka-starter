package org.age.akka.core.messages.node.messaging;

public class RandomNeighborRequest extends SerializableContentMessage<String> {

    public RandomNeighborRequest(String content) {
        super(content);
    }

}
