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
        receive(ReceiveBuilder
                        .match(Command.class, InnerActor.this::command)
                        .matchAny(o -> log.info("unknown message"))
                        .build()
        );
    }



    @Override
    public void postStop() throws Exception {
        System.out.println("Stopper");
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        System.out.println("Restarted");
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
        log.info("Creating actor " + command.getParam());
        context().actorOf(Props.create(OuterActor.class), command.getParam());
    }

    private void listExistingActors() {
        log.info("Listing actors");
        JavaConversions.asJavaCollection(context().children()).stream().forEach(a -> {
            System.out.println(a.isTerminated() + ": " + a);
        });

    }

}
