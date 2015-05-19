package org.age.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.collection.Iterator;

public class InnerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    public InnerActor() {
        receive(ReceiveBuilder
                        .match(Command.class, InnerActor.this::command)
                        .matchAny(o -> log.info("unknown message"))
                        .build()
        );
    }


    private void command(Command command) {
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
        Iterator<ActorRef> actors = context().children().toIterator().filter(e -> !e.isTerminated());

        while (actors.hasNext()) {
            ActorRef actor = actors.next();
            System.out.println(actor.path());
        }
    }

}
