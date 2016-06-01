package org.age.akka.core.messages;

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public abstract class DoubleValueMessage<T extends Serializable, U extends Serializable> implements Message {

    private final T firstValue;

    private final U secondValue;

    public DoubleValueMessage(T firstValue, U secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    public T getFirstValue() {
        return firstValue;
    }

    public U getSecondValue() {
        return secondValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DoubleValueMessage<?, ?> that = (DoubleValueMessage<?, ?>) o;

        return new EqualsBuilder()
                .append(firstValue, that.firstValue)
                .append(secondValue, that.secondValue)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstValue)
                .append(secondValue)
                .toHashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("firstValue", firstValue)
                .add("secondValue", secondValue)
                .toString();
    }
}
