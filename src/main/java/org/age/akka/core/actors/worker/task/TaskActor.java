package org.age.akka.core.actors.worker.task;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.japi.pf.UnitPFBuilder;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.age.akka.core.messages.node.lifecycle.TaskInterruptedRequest;
import org.age.akka.core.messages.worker.nodes.StartWorkerTaskRequest;
import org.age.akka.core.messages.node.lifecycle.TaskInterruptedResponse;
import org.age.akka.core.messages.node.messaging.BroadcastRequest;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class TaskActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    protected final Cluster cluster = Cluster.get(getContext().system());

    protected boolean cancelled;

    protected boolean paused;

    protected UUID uuid = UUID.randomUUID();

    public UnitPFBuilder<Object> getDefaultReceiveBuilder() {
        return ReceiveBuilder
                .match(StartWorkerTaskRequest.class, this::processStartTaskRequest);
    }

    private void processStartTaskRequest(StartWorkerTaskRequest msg) throws Exception {
        log.debug("received start task request {}", msg);
        if (paused) {
            log.info("unpausing task");
            paused = false;
        }
        doTask();
    }

    protected abstract void doTask() throws Exception;

    protected void checkIfInterrupted() throws Exception {
        Timeout timeout = new Timeout(Duration.create(15, TimeUnit.SECONDS));
        Future<Object> isInterrupted = Patterns.ask(context().parent(), new TaskInterruptedRequest(), timeout);
        TaskInterruptedResponse isInterruptedResult = (TaskInterruptedResponse) Await.result(isInterrupted, timeout.duration());
        log.debug("checked if is interrupted {}", isInterruptedResult);

        cancelled = isInterruptedResult.isCancelled();
        paused = isInterruptedResult.isPaused();
    }

    protected void broadcast(BroadcastRequest broadcastWorkerMsg) {
        context().parent().tell(broadcastWorkerMsg, self());
    }

}
