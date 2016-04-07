package org.age.akka.start.cluster.participant.message.listener;

import com.hazelcast.core.Message;
import org.age.akka.core.NodeStarter;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.data.NodeId;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.message.ClusterStartMessageType;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.age.akka.start.cluster.enums.StartupProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletableFuture;

import static org.age.akka.start.common.message.ClusterStartMessageType.*;

@Named("workerNodeStartMessageListener")
public class ClusterParticipantNodeStartMessageListener extends AbstractMessageListener {

    private static final Logger log = LoggerFactory.getLogger(ClusterParticipantNodeStartMessageListener.class);

    @Inject
    private NodeStarter nodeStarter;

    @Override
    public void onMessage(Message<ClusterStartMessage> message) {
        ClusterStartMessage clusterStartMessage = message.getMessageObject();
        log.info("Received message " + clusterStartMessage);

        switch (clusterStartMessage.getClusterStartMessageType()) {
            case START_CLUSTER:
                startCluster(clusterStartMessage.getSenderId());
                break;
            case JOIN_CLUSTER:
                joinCluster(clusterStartMessage.getSenderId());
                break;
            case CREATE_WORKER:
                createWorker(clusterStartMessage.getSenderId());
                break;
            case EXIT_APPLICATION:
                exitApplication();
                break;
            default:
                log.warn("Wrong message type for node: ", clusterStartMessage.getClusterStartMessageType());
        }
    }

    private void startCluster(NodeId senderId) {
        log.trace("Starting cluster");
        ClusterConfigHolder configHolder = updateCurrentNodeConfig((ClusterConfigHolder) management().get(StartupProps.CLUSTER_CONFIG));

        CompletableFuture.supplyAsync(() -> nodeStarter.startCluster(configHolder))
                .thenAccept(startedCluster -> {
                    log.info("Started cluster: ", startedCluster);
                    dataHolder.setActorSystem(startedCluster.getActorSystem());

                    ClusterStartMessageType type = startedCluster.isClusterStarted() ? CLUSTER_START_SUCCEEDED : CLUSTER_START_FAILED;
                    sendMessage(senderId, type);
                });
    }

    private void joinCluster(NodeId senderId) {
        log.trace("Joining cluster");
        ClusterConfigHolder configHolder = updateCurrentNodeConfig((ClusterConfigHolder) management().get(StartupProps.CLUSTER_CONFIG));

        CompletableFuture.supplyAsync(() -> nodeStarter.joinCluster(configHolder))
                .thenAccept(startedCluster -> {
                    log.info("Joining cluster: ", startedCluster);
                    dataHolder.setActorSystem(startedCluster.getActorSystem());

                    ClusterStartMessageType type = startedCluster.isClusterStarted() ? CLUSTER_JOIN_SUCCEEDED : CLUSTER_JOIN_FAILED;
                    sendMessage(senderId, type);

                    if (type == CLUSTER_JOIN_FAILED) {
                        log.warn("Cluster joining failed.", nodeId);
                        System.exit(1);
                    }
                });
    }

    private void createWorker(NodeId senderId) {
        log.trace("Creating worker");
        ClusterConfigHolder configHolder = updateCurrentNodeConfig((ClusterConfigHolder) management().get(StartupProps.CLUSTER_CONFIG));

        CompletableFuture.supplyAsync(() -> nodeStarter.createWorker(configHolder))
                .thenAccept(actorSystem -> {
                    log.info("Setting actor system: ", actorSystem);
                    dataHolder.setActorSystem(actorSystem);

                    ClusterStartMessageType type = actorSystem == null ? CREATE_WORKER_FAILED : CREATE_WORKER_SUCCEEDED;
                    sendMessage(senderId, type);

                    if (type == CREATE_WORKER_FAILED) {
                        log.warn("Could not create worker.", nodeId);
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
                .withCurrentNode(nodes().get(nodeId))
                .build();
    }

    private void sendMessage(NodeId senderId, ClusterStartMessageType type) {
        log.trace("Send message");
        topic(senderId).publish(ClusterStartMessage.builder()
                .withSenderId(nodeId)
                .withClusterStartMessageType(type)
                .build());
    }

    private void exitApplication() {
        log.trace("Exiting application");
        System.exit(0);
    }

}
