package org.age.akka.start.cluster.message.listener;

import com.hazelcast.core.Message;
import org.age.akka.core.NodeStarter;
import org.age.akka.start.cluster.enums.ManagementMapProperties;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.message.ClusterStartMessageType;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletableFuture;

import static org.age.akka.start.common.message.ClusterStartMessageType.*;

@Named("slaveListener")
public class SlaveStartMessageListener extends AbstractMessageListener {

    private static final Logger log = LoggerFactory.getLogger(SlaveStartMessageListener.class);

    @Inject
    private NodeStarter nodeStarter;

    @Override
    public void onMessage(Message<ClusterStartMessage> message) {
        ClusterStartMessage clusterStartMessage = message.getMessageObject();
        log.info("Received message " + clusterStartMessage);

        switch (clusterStartMessage.getClusterStartMessageType()) {
            case START_CLUSTER:
                startCluster();
                break;
            case JOIN_CLUSTER:
                joinCluster();
                break;
            case CREATE_WORKER:
                createWorker();
                break;
            case EXIT_APPLICATION:
                exitApplication();
                break;
            default:
                log.warn("Wrong message type for node: ", clusterStartMessage.getClusterStartMessageType());
        }
    }

    private void startCluster() {
        log.trace("Starting cluster");
        ClusterConfigHolder configHolder = updateCurrentNodeConfig((ClusterConfigHolder) management().get(ManagementMapProperties.CLUSTER_CONFIG));

        CompletableFuture.supplyAsync(() -> nodeStarter.startCluster(configHolder))
                .thenAccept(startedCluster -> {
                    log.info("Started cluster: ", startedCluster);
                    dataHolder.setActorSystem(startedCluster.getActorSystem());

                    ClusterStartMessageType type = startedCluster.isClusterStarted() ? CLUSTER_START_SUCCEEDED : CLUSTER_START_FAILED;
                    sendMessageToMaster(type);
                });
    }

    private void joinCluster() {
        log.trace("Joining cluster");
        ClusterConfigHolder configHolder = updateCurrentNodeConfig((ClusterConfigHolder) management().get(ManagementMapProperties.CLUSTER_CONFIG));

        CompletableFuture.supplyAsync(() -> nodeStarter.joinCluster(configHolder))
                .thenAccept(startedCluster -> {
                    log.info("Joining cluster: ", startedCluster);
                    dataHolder.setActorSystem(startedCluster.getActorSystem());

                    ClusterStartMessageType type = startedCluster.isClusterStarted() ? CLUSTER_JOIN_SUCCEEDED : CLUSTER_JOIN_FAILED;
                    sendMessageToMaster(type);

                    if (type == CLUSTER_JOIN_FAILED) {
                        log.warn("Cluster joining failed.", myUUID());
                        System.exit(1);
                    }
                });
    }

    private void createWorker() {
        log.trace("Creating worker");
        if (dataHolder.getCreatingWorker()) {
            log.info("Worker is already being created");
            return;
        }

        ClusterConfigHolder configHolder = updateCurrentNodeConfig((ClusterConfigHolder) management().get(ManagementMapProperties.CLUSTER_CONFIG));

        CompletableFuture.supplyAsync(() -> nodeStarter.createWorker(configHolder))
                .thenAccept(actorSystem -> {
                    log.info("Setting actor system: ", actorSystem);
                    dataHolder.setActorSystem(actorSystem);

                    ClusterStartMessageType type = actorSystem == null ? CREATE_WORKER_FAILED : CREATE_WORKER_SUCCEEDED;
                    sendMessageToMaster(type);

                    if (type == CREATE_WORKER_FAILED) {
                        log.warn("Could not create worker.", myUUID());
                        System.exit(1);
                    }
                });
    }

    private ClusterConfigHolder updateCurrentNodeConfig(ClusterConfigHolder configHolder) {
        if (configHolder == null) {
            return null;
        }

        return ClusterConfigHolder.builder()
                .withClusterNodes(configHolder.getClusterNodes())
                .withCurrentNode(nodes().get(myUUID()))
                .build();
    }

    private void sendMessageToMaster(ClusterStartMessageType type) {
        sendMessage("master", type);
    }

    private void sendMessage(String topicName, ClusterStartMessageType type) {
        log.trace("Send message ", type);
        topic(topicName).publish(ClusterStartMessage.builder()
                .withSenderUUID(myUUID())
                .withClusterStartMessageType(type)
                .build());
    }

    private void exitApplication() {
        log.trace("Exiting application");
        System.exit(0);
    }

}
