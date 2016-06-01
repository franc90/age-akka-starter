package org.age.akka.core.messages.node.lifecycle;

import org.age.akka.core.messages.DoubleValueMessage;

public class TaskInterruptedResponse extends DoubleValueMessage<Boolean, Boolean> {

    public TaskInterruptedResponse(boolean paused, boolean cancelled) {
        super(paused, cancelled);
    }

    public boolean isPaused() {
        return getFirstValue();
    }

    public boolean isCancelled() {
        return getSecondValue();
    }

}