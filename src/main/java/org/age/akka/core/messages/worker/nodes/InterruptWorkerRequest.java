package org.age.akka.core.messages.worker.nodes;

import org.age.akka.core.messages.DoubleValueMessage;

public class InterruptWorkerRequest extends DoubleValueMessage<Boolean, Boolean> {

    public InterruptWorkerRequest(boolean pause, boolean cancel) {
        super(pause, cancel);
    }

    public boolean isPause() {
        return getFirstValue();
    }

    public boolean isCancel() {
        return getSecondValue();
    }

}
