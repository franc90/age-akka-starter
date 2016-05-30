package org.age.akka.core.actors.messages.node;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class StartTaskMsg implements Serializable {

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .toString();
    }
}
