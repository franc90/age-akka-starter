package org.age.akka.start.startup.worker;

import akka.actor.ActorSystem;
import org.age.akka.core.WorkerCreator;
import org.age.akka.start.common.utils.ClusterDataHolder;
import org.age.akka.start.common.utils.HazelcastBean;
import org.age.akka.start.startup.StartupState;
import org.age.akka.start.startup.enums.StartupProps;
import org.age.akka.start.startup.worker.initialization.WorkerNodeInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Named
public class WorkerNodeStarter extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(WorkerNodeStarter.class);

    @Inject
    private WorkerNodeInitializer workerNodeInitializer;

    @Inject
    private WorkerCreator workerCreator;

    @Inject
    private ClusterDataHolder clusterDataHolder;

    public void startWork() throws UnknownHostException, InterruptedException {
        workerNodeInitializer.initialize();

        while (true) {
            StartupState status = (StartupState) management().get(StartupProps.STATUS);

            if (status == null || status == StartupState.INIT) {
                log.trace("Waiting for cluster initialization");
            } else if (status == StartupState.WORKING) {
                if (!clusterDataHolder.isWorkerCreated()) {
                    ActorSystem actorSystem = workerCreator.createActorSystem();
                    clusterDataHolder.setActorSystem(actorSystem);
                }
            } else if (status == StartupState.FINISHED) {
                log.trace("Work finished, exiting");
                System.exit(0);
            }
            TimeUnit.MILLISECONDS.sleep(250);
        }
    }
}
