package org.age.akka.core.actors.master.services;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.messages.task.UpdateTaskStateRequest;
import org.age.akka.core.messages.task.UpdateTaskStateResponse;

import java.util.Optional;

import static org.age.akka.core.messages.task.UpdateTaskStateResponse.State.*;

public class TaskServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private UpdateTaskStateResponse.State state = INIT;

    public TaskServiceActor() {
        receive(ReceiveBuilder
                .match(UpdateTaskStateRequest.class, this::processUpdateTaskStateRequest)
                .matchAny(msg -> log.warning("Received not supported message {}", msg))
                .build());

    }

    private void processUpdateTaskStateRequest(UpdateTaskStateRequest request) {
        log.debug("update task state {}", request);
        switch (request.getType()) {
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
        if (state == INIT || state == CANCELLED) {
            log.debug("starting workers");
            sendStartWorkerMessages();
            state = STARTED_OR_RESUMED;
        } else {
            log.debug("tasks do not need starting");
        }
    }

    private void sendStartWorkerMessages() {
        log.debug("send start task for all workers request to worker service");
        findWorkerService().ifPresent(workerService -> {
            UpdateTaskStateRequest request = new UpdateTaskStateRequest(UpdateTaskStateRequest.Type.START);
            workerService.tell(request, self());
        });
    }

    private void pauseWorkers() {
        if (state == STARTED_OR_RESUMED) {
            log.debug("tasks need pausing");
            sendPauseWorkerMessages();
            state = PAUSED;
        } else {
            log.debug("current tasks' state is {}. No pausing needed. Replying master with PAUSED", state);
        }
        workersPaused();
    }

    private void sendPauseWorkerMessages() {
        log.debug("send request to pause all workers");
        findWorkerService().ifPresent(workerService -> {
            UpdateTaskStateRequest request = new UpdateTaskStateRequest(UpdateTaskStateRequest.Type.PAUSE);
            workerService.tell(request, self());
        });
    }

    private void workersPaused() {
        log.debug("inform parent that all workers have been stopped");
        context().parent().tell(new UpdateTaskStateResponse(PAUSED), self());
    }

    private void resumeWorkers() {
        if (state == PAUSED || state == INIT || state == CANCELLED) {
            log.debug("tasks not running, send request to start them");
            sendResumeWorkerMessages();
            state = STARTED_OR_RESUMED;
        } else {
            log.debug("no resuming needed in {} state", state);
        }
    }

    private void sendResumeWorkerMessages() {
        log.debug("send resume workers message");
        findWorkerService().ifPresent(workerService -> {
            UpdateTaskStateRequest request = new UpdateTaskStateRequest(UpdateTaskStateRequest.Type.RESUME);
            workerService.tell(request, self());
        });
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
