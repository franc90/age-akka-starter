package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.messages.task.State;
import org.age.akka.core.actors.messages.task.TaskMsg;

public class TaskServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    private State state = State.INIT;

    public TaskServiceActor() {
        receive(ReceiveBuilder
                .match(TaskMsg.class, this::updateState)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    private void updateState(TaskMsg msg) {
        log.info("Received update state message " + msg);
        switch (msg.getType()) {
            case PAUSE:
                pauseWorkers();
            case RESUME:
                resumeWorkers();
        }
    }

    private void pauseWorkers() {
        if (state == State.RESUMED || state == State.STARTED) {
            log.info("Task needs pausing");
            sendPauseWorkerMessages();
        } else {
            log.info("task already " + state + ". Confirming with PAUSED");
        }
        sender().tell(State.PAUSED, self());
    }

    private void sendPauseWorkerMessages() {

    }

    private void resumeWorkers() {
        if (state == State.PAUSED) {
            log.info("resuming workers");
            sendResumeWorkerMessages();
        } else {
            log.info("no resuming needed in " + state);
        }
    }

    private void sendResumeWorkerMessages() {

    }
}
