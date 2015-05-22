package org.age.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.collection.JavaConversions;

public class InnerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public InnerActor() {
        super();
        receive(ReceiveBuilder
                        .match(Command.class, InnerActor.this::command)
                        .matchAny(o -> log.info("unknown message"))
                        .build()
        );
    }


    private void command(Command command) {
        log.info("executing command: {}", command);
        if (Command.Type.NEW.equals(command.getType())) {
            createNewActor(command);
        }

        if (Command.Type.LIST.equals(command.getType())) {
            listExistingActors();
        }
    }

    private void createNewActor(Command command) {
        context().actorOf(Props.create(OuterActor.class), command.getParam());
    }

    private void listExistingActors() {
        JavaConversions.asJavaCollection(context().children()).stream().filter(c -> !c
                .isTerminated()).forEach(System.out::println);
    }

}
