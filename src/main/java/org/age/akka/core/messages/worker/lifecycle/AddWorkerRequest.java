package org.age.akka.core.messages.worker.lifecycle;

import akka.actor.Address;
import org.age.akka.core.messages.SingleValueMessage;

public class AddWorkerRequest extends SingleValueMessage<Address> implements UpdateWorkersRequest {

    public AddWorkerRequest(Address workerAddress) {
        super(workerAddress);
    }

    public Address getWorkerAddress() {
        return getValue();
    }

}
