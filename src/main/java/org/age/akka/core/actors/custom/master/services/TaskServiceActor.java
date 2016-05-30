package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.messages.task.StateMsg;
import org.age.akka.core.actors.messages.task.TaskStateMsg;
import org.age.akka.core.actors.messages.worker.WorkersPausedMsg;

import java.util.Optional;

public class TaskServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    private StateMsg state = StateMsg.INIT;

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
            case START:
                startWorkers();
                break;
            case PAUSE:
                pauseWorkers();
                break;
            case RESUME:
                resumeWorkers();
                break;
        }
    }

    private void startWorkers() {
        if (state == StateMsg.INIT || state == StateMsg.CANCELLED) {
            log.info("starting workers");
            sendStartWorkerMessages();
            state = StateMsg.STARTED_OR_RESUMED;
        } else {
            log.info("tasks do not need starting");
        }
    }

    private void sendStartWorkerMessages() {
        log.info("send start all workers message");
        findWorkerService().ifPresent(workerService -> workerService.tell(new TaskStateMsg(TaskStateMsg.Type.START), self()));
    }

    private void pauseWorkers() {
        if (state == StateMsg.STARTED_OR_RESUMED) {
            log.info("task needs pausing");
            sendPauseWorkerMessages();
            state = StateMsg.PAUSED;
        } else {
            log.info("task " + state + ". No pausing needed. Replying master with PAUSED");
        }
        workersPaused(null);
    }

    private void sendPauseWorkerMessages() {
        log.info("send pause all workers message");
        findWorkerService().ifPresent(workerService -> workerService.tell(new TaskStateMsg(TaskStateMsg.Type.PAUSE), self()));
    }

    private void workersPaused(WorkersPausedMsg msg) {
        log.info("all workers paused - inform master");
        context().parent().tell(StateMsg.PAUSED, self());

    }

    private void resumeWorkers() {
        if (state == StateMsg.PAUSED || state == StateMsg.INIT || state == StateMsg.CANCELLED) {
            log.info("workers paused. Resume working");
            sendResumeWorkerMessages();
            state = StateMsg.STARTED_OR_RESUMED;
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
