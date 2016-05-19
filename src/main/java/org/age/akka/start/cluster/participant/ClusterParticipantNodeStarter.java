package org.age.akka.start.cluster.participant;

import akka.actor.ActorSystem;
import org.age.akka.core.NodeStarter;
import org.age.akka.start.cluster.enums.ClusterStatus;
import org.age.akka.start.cluster.enums.ManagementMapProperties;
import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.data.Hostname;
import org.age.akka.start.common.data.Port;
import org.age.akka.start.common.enums.ClusterProps;
import org.age.akka.start.common.exception.NodeInitializationException;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.age.akka.start.common.utils.ClusterDataHolder;
import org.age.akka.start.common.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Named
public class ClusterParticipantNodeStarter extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterParticipantNodeStarter.class);

    private static final int START_RETRIES = 10;

    @Value("#{'${network.interface.names:default}'.split(',')}")
    private Set<String> networkInterfaceNames;

    @Inject
    @Named("workerNodeStartMessageListener")
    private AbstractMessageListener messageListener;

    @Inject
    private NodeStarter nodeStarter;

    @Inject
    private ClusterDataHolder clusterDataHolder;

    public void startAkkaCluster() throws UnknownHostException, InterruptedException {
        AkkaNode currentNode = populateNodeData();

        while (true) {
            ClusterStatus status = (ClusterStatus) management().get(ManagementMapProperties.STATUS);

            if (status == null || status == ClusterStatus.INITIALIZING) {
                log.trace("Waiting for cluster initialization");
            } else if (status == ClusterStatus.WORKING) {
                if (!clusterDataHolder.getCreatingWorker()) {
                    log.info("Creating worker");
                    ClusterConfigHolder configHolder = (ClusterConfigHolder) management().get(ManagementMapProperties.CLUSTER_CONFIG);

                    ActorSystem actorSystem = nodeStarter.createWorker(ClusterConfigHolder.builder()
                            .withCurrentNode(currentNode)
                            .withClusterNodes(configHolder.getClusterNodes())
                            .build());

                    clusterDataHolder.setActorSystem(actorSystem);
                    return;
                } else {
                    log.info("Cluster created");
                    return;
                }
            } else if (status == ClusterStatus.SHUT_DOWN) {
                log.trace("Cluster shut down");
                System.exit(0);
            }
            TimeUnit.MILLISECONDS.sleep(250);
        }
    }

    private AkkaNode populateNodeData() {
        log.trace("initializing node");
        Hostname hostname = new Hostname(networkInterfaceNames);
        log.trace("hostname " + hostname);

        int startTry = 0;
        while (startTry++ < START_RETRIES) {
            Port port = new Port();
            log.trace("port " + port);

            long conflictingHosts = nodes()
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .filter(e -> e.getActorSystemName().equals(ClusterProps.ACTOR_SYSTEM_NAME))
                    .filter(e -> e.getHostname().equals(hostname))
                    .filter(e -> e.getPort().equals(port))
                    .count();

            if (conflictingHosts == 0) {
                AkkaNode akkaNode = AkkaNode.builder()
                        .withActorSystemName(ClusterProps.ACTOR_SYSTEM_NAME)
                        .withHostname(hostname)
                        .withPort(port)
                        .build();

                nodes().put(getNodeUUID(), akkaNode);
                topic(getNodeUUID()).addMessageListener(messageListener);

                log.info("Node initialization succeeded. NodeUUID = " + getNodeUUID() + " " + akkaNode);
                return akkaNode;
            }

            log.info("Node initialization try " + startTry + "of " + START_RETRIES + " failed.");
        }

        throw new NodeInitializationException("Could not find free port for node");
    }
}
