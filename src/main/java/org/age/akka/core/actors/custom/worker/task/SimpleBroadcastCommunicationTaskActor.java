package org.age.akka.core.actors.custom.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.core.actors.messages.worker.TaskBroadcastInMsg;
import org.age.akka.core.actors.messages.worker.TaskBroadcastOutMsg;
import org.age.akka.core.actors.messages.worker.TaskResumeMsg;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class SimpleBroadcastCommunicationTaskActor extends TaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private boolean finished;

    private int counter;

    public SimpleBroadcastCommunicationTaskActor() {
        receive(getDefaultReceiveBuilder()
                .match(TaskBroadcastInMsg.class, this::onMessage)
                .match(TaskResumeMsg.class, msg -> doTask())
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

            broadcast(new TaskBroadcastOutMsg(counter + ". Test message from " + uuid.toString()));

            FiniteDuration duration = Duration.create(3, TimeUnit.SECONDS);
            context().system().scheduler().scheduleOnce(duration, self(), new TaskResumeMsg(), context().system().dispatcher(), null);
            return;
        }

        finished = true;
    }

    private void onMessage(TaskBroadcastInMsg msg) {
        log.info("Message received: {}.", msg.getContent());
    }
}
