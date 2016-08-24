package org.age.akka.core.actors.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.core.actors.master.services.LifecycleServiceActor;
import org.age.akka.core.helper.TimeUtils;
import org.age.akka.core.messages.node.lifecycle.ResumeTaskRequest;
import org.age.akka.core.messages.node.messaging.RandomNeighborRequest;
import org.age.akka.core.messages.node.messaging.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RandomRecipientMessageSendingTaskActor extends TaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private static final Logger logger = LoggerFactory.getLogger(LifecycleServiceActor.class);

    private boolean finished;

    private int counter;

    public RandomRecipientMessageSendingTaskActor() {
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

        if (counter++ < Integer.MAX_VALUE) {
            log.info("Iteration {}. {} sending message.", counter, uuid.toString());

            long timestamp = System.currentTimeMillis();
            String uuid = UUID.randomUUID().toString();
            String msg = timestamp + "," + uuid;
            logger.warn("{},snd,{}", TimeUtils.toString(timestamp), msg);

            sendMessage(new RandomNeighborRequest(msg));

            FiniteDuration duration = Duration.create(750, TimeUnit.MILLISECONDS);
            context().system().scheduler().scheduleOnce(duration, self(), new ResumeTaskRequest(), context().system().dispatcher(), null);
            return;
        }

        finished = true;
    }

    protected void sendMessage(RandomNeighborRequest msg) {
        context().parent().tell(msg, self());
    }

    private void onMessage(TaskMessage msg) {
        long timestamp = System.currentTimeMillis();
        logger.warn("{},rcv,{},{}", TimeUtils.toString(timestamp), timestamp, msg.getContent());
        log.info("Message received: {}.", msg.getContent());
    }

}
