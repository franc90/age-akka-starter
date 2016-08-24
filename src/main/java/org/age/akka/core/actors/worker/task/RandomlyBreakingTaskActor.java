package org.age.akka.core.actors.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.core.actors.master.services.LifecycleServiceActor;
import org.age.akka.core.helper.TimeUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomlyBreakingTaskActor extends SimpleLongRunningTaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private static final Logger logger = LoggerFactory.getLogger(LifecycleServiceActor.class);

    private final static int INITIAL_ITERATIONS = 10;

    private final static double EXCEPTION_PROBABILITY = .3;

    @Override
    protected void additionalAction(int iteration) {
        if (iteration > INITIAL_ITERATIONS) {
            double randomValue = RandomUtils.nextDouble(0.0, 1.0);
            if (randomValue < EXCEPTION_PROBABILITY) {
                long timestamp = System.currentTimeMillis();
                logger.warn("{},ext,{},{}", TimeUtils.toString(timestamp), timestamp, self().path().toString());
                log.debug("{} < {}, exiting", randomValue, EXCEPTION_PROBABILITY);
                System.exit(0);
            }
        }
    }
}
