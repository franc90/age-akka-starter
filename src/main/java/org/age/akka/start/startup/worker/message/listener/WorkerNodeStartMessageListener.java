package org.age.akka.start.startup.worker.message.listener;

import com.hazelcast.core.Message;
import org.age.akka.core.ClusterCreator;
import org.age.akka.core.WorkerCreator;
import org.age.akka.start.common.data.NodeId;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.message.ClusterStartMessageType;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletableFuture;

import static org.age.akka.start.common.message.ClusterStartMessageType.*;

@Named("workerNodeStartMessageListener")
public class WorkerNodeStartMessageListener extends AbstractMessageListener {

    private static final Logger log = LoggerFactory.getLogger(WorkerNodeStartMessageListener.class);

    @Inject
    private ClusterCreator clusterCreator;

    @Inject
    private WorkerCreator workerCreator;

    @Override
    public void onMessage(Message<ClusterStartMessage> message) {
        ClusterStartMessage clusterStartMessage = message.getMessageObject();
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
        CompletableFuture.supplyAsync(() -> clusterCreator.startCluster())
                .thenAccept(startedCluster -> {
                    log.info("Started cluster: ", startedCluster);
                    dataHolder.setActorSystem(startedCluster.getActorSystem());

                    ClusterStartMessageType type = startedCluster.isClusterStarted() ? CLUSTER_START_SUCCEEDED : CLUSTER_START_FAILED;
                    sendMessage(senderId, type);
                });
    }

    private void joinCluster(NodeId senderId) {
        CompletableFuture.supplyAsync(() -> clusterCreator.joinCluster())
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
        CompletableFuture.supplyAsync(() -> workerCreator.createActorSystem())
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

    private void sendMessage(NodeId senderId, ClusterStartMessageType type) {
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
