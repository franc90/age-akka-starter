package org.age.akka.core.actors.worker.task;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.lang3.RandomUtils;

public class RandomlyBreakingTaskActor extends SimpleLongRunningTaskActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final static int INITIAL_ITERATIONS = 10;

    private final static double EXCEPTION_PROBABILITY = .3;

    @Override
    protected void additionalAction(int iteration) {
        if (iteration > INITIAL_ITERATIONS) {
            double randomValue = RandomUtils.nextDouble(0.0, 1.0);
            if (randomValue < EXCEPTION_PROBABILITY) {
                log.debug("{} < {}, exiting", randomValue, EXCEPTION_PROBABILITY);
                System.exit(0);
            }
        }
    }
}
