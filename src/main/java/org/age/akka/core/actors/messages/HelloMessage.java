package org.age.akka.core.actors.messages;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HelloMessage implements Message<String> {

    private final String text;

    public HelloMessage(String text) {
        this.text = text;
    }

    @Override
    public String getContent() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HelloMessage that = (HelloMessage) o;

        return new EqualsBuilder()
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(text)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("text", text)
                .toString();
    }
}
