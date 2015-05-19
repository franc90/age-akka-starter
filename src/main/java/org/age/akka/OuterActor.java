package org.age.akka;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class OuterActor extends AbstractActor {

    public OuterActor() {
        receive(ReceiveBuilder.matchAny(e -> System.out.println("matched any")).build());
        System.out.println("Outer Actor built");
    }

}
