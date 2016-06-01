package org.age.akka.core.actors.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.core.messages.node.lifecycle.ResumeTaskRequest;
import org.age.akka.core.messages.node.messaging.BroadcastRequest;
import org.age.akka.core.messages.node.messaging.TaskMessage;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class SimpleBroadcastCommunicationTaskActor extends TaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private boolean finished;

    private int counter;

    public SimpleBroadcastCommunicationTaskActor() {
        receive(getDefaultReceiveBuilder()
                .match(TaskMessage.class, this::onMessage)
                .match(ResumeTaskRequest.class, request -> doTask())
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    protected void doTask() throws Exception {
        checkIfInterrupted();
        if (paused || cancelled || finished) {
            return;
        }

        if (counter++ < 100) {
            log.info("Iteration {}. {} sending message.", counter, uuid.toString());

            broadcast(new BroadcastRequest<>(counter + ". Test message from " + uuid.toString()));

            FiniteDuration duration = Duration.create(3, TimeUnit.SECONDS);
            context().system().scheduler().scheduleOnce(duration, self(), new ResumeTaskRequest(), context().system().dispatcher(), null);
            return;
        }

        finished = true;
    }

    private void onMessage(TaskMessage msg) {
        log.info("Message received: {}.", msg.getContent());
    }
}
