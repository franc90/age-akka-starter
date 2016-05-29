package org.age.akka.core.actors.messages;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SendMsgMessage<T extends Serializable> implements Message<Message<T>> {

    private final Message<T> content;

    public SendMsgMessage(Message<T> content) {
        this.content = content;
    }

    @Override
    public Message<T> getContent() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SendMsgMessage that = (SendMsgMessage) o;

        return new EqualsBuilder()
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(content)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("content", content)
                .toString();
    }
}
