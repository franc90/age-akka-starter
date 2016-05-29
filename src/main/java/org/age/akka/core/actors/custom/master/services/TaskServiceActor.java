package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.messages.task.State;
import org.age.akka.core.actors.messages.task.TaskStateMsg;
import org.age.akka.core.actors.messages.worker.WorkersPausedMsg;

import java.util.Optional;

public class TaskServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    private State state = State.INIT;

    public TaskServiceActor() {
        receive(ReceiveBuilder
                .match(TaskStateMsg.class, this::updateTaskState)
                .match(WorkersPausedMsg.class, this::workersPaused)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());

    }

    private void updateTaskState(TaskStateMsg msg) {
        log.info("update task state " + msg);
        switch (msg.getType()) {
            case PAUSE:
                pauseWorkers();
                break;
            case RESUME:
                resumeWorkers();
        }
    }

    private void pauseWorkers() {
        if (state == State.STARTED_OR_RESUMED) {
            log.info("task needs pausing");
            sendPauseWorkerMessages();
            state = State.PAUSED;
        } else {
            log.info("task " + state + ". No pausing needed. Replying master with PAUSED");
            workersPaused(null);
        }
    }

    private void sendPauseWorkerMessages() {
        log.info("send pause all workers message");
        findWorkerService().ifPresent(workerService -> workerService.tell(new TaskStateMsg(TaskStateMsg.Type.PAUSE), self()));
    }

    private void workersPaused(WorkersPausedMsg msg) {
        log.info("all workers paused - inform master");
        context().parent().tell(State.PAUSED, self());

    }

    private void resumeWorkers() {
        if (state == State.PAUSED) {
            log.info("workers paused. Resume working");
            sendResumeWorkerMessages();
            state = State.STARTED_OR_RESUMED;
        } else {
            log.info("no resuming needed in " + state);
        }
    }

    private void sendResumeWorkerMessages() {
        log.info("send resume workers message");
        findWorkerService().ifPresent(workerService -> workerService.tell(new TaskStateMsg(TaskStateMsg.Type.RESUME), self()));
    }

    private Optional<ActorSelection> findWorkerService() {
        ActorSelection workerService = context().actorSelection("../workerService");
        if (workerService == null) {
            log.warning("No worker service found");
            return Optional.empty();
        }
        return Optional.of(workerService);
    }
}
