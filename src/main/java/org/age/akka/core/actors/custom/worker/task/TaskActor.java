package org.age.akka.core.actors.custom.worker.task;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.japi.pf.UnitPFBuilder;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.age.akka.core.actors.messages.node.IsTaskInterruptedMsg;
import org.age.akka.core.actors.messages.node.StartTaskMsg;
import org.age.akka.core.actors.messages.node.TaskInterruptedResponseMsg;
import org.age.akka.core.actors.messages.worker.TaskBroadcastOutMsg;
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
                .match(StartTaskMsg.class, this::startTask);
//                .matchAny(msg -> log.info("Received not supported message {}", msg));
    }

    protected void startTask(StartTaskMsg msg) throws Exception {
        log.info("received start task message {}", msg);
        if (paused) {
            log.info("unpausing task");
            paused = false;
        }
        doTask();
    }

    protected abstract void doTask() throws Exception;

    protected void checkIfInterrupted() throws Exception {
        Timeout timeout = new Timeout(Duration.create(15, TimeUnit.SECONDS));
        Future<Object> isInterrupted = Patterns.ask(context().parent(), new IsTaskInterruptedMsg(), timeout);
        TaskInterruptedResponseMsg isInterruptedResult = (TaskInterruptedResponseMsg) Await.result(isInterrupted, timeout.duration());
        log.info("checked if is interrupted {}", isInterruptedResult);

        cancelled = isInterruptedResult.isCancelled();
        paused = isInterruptedResult.isPaused();
    }

    protected void broadcast(TaskBroadcastOutMsg broadcastWorkerMsg) {
        context().parent().tell(broadcastWorkerMsg, self());
    }

}
