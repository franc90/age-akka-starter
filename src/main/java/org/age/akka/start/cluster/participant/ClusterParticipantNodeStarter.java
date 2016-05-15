package org.age.akka.start.cluster.participant;

import akka.actor.ActorSystem;
import org.age.akka.core.NodeStarter;
import org.age.akka.start.cluster.participant.initialization.ClusterParticipantNodeInitializer;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.utils.ClusterDataHolder;
import org.age.akka.start.common.utils.HazelcastBean;
import org.age.akka.start.cluster.StartupState;
import org.age.akka.start.cluster.enums.StartupProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Named
public class ClusterParticipantNodeStarter extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterParticipantNodeStarter.class);

    @Inject
    private ClusterParticipantNodeInitializer clusterParticipantNodeInitializer;

    @Inject
    private NodeStarter nodeStarter;

    @Inject
    private ClusterDataHolder clusterDataHolder;

    public void startWork() throws UnknownHostException, InterruptedException {
        clusterParticipantNodeInitializer.initialize();

        while (true) {
            StartupState status = (StartupState) management().get(StartupProps.STATUS);

            if (status == null || status == StartupState.INITIALIZE_CLUSTER) {
                log.trace("Waiting for cluster initialization");
            } else if (status == StartupState.CLUSTER_WORKING) {
                if (!clusterDataHolder.isWorkerCreated()) {
                    ClusterConfigHolder configHolder = (ClusterConfigHolder) management().get(StartupProps.CLUSTER_CONFIG);
                    ActorSystem actorSystem = nodeStarter.createWorker(configHolder);
                    clusterDataHolder.setActorSystem(actorSystem);
                }
            } else if (status == StartupState.CLUSTER_INITIALIZATION_FINISHED) {
                log.trace("Work finished, exiting");
                System.exit(0);
            }
            TimeUnit.MILLISECONDS.sleep(250);
        }
    }
}
