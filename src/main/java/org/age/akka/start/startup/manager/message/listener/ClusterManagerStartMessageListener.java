package org.age.akka.start.startup.manager.message.listener;

import com.hazelcast.core.Message;
import org.age.akka.start.common.data.NodeId;
import org.age.akka.start.common.enums.ClusterProps;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.message.ClusterStartMessageType;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.age.akka.start.startup.StartupState;
import org.age.akka.start.startup.enums.StartupProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@Named("org.age.akka.start.startup.manager.message.listener.ClusterManagerStartMessageListener")
public class ClusterManagerStartMessageListener extends AbstractMessageListener {

    private static final Logger log = LoggerFactory.getLogger(ClusterManagerStartMessageListener.class);

    @Override
    public void onMessage(Message<ClusterStartMessage> message) {
        ClusterStartMessage clusterStartMessage = message.getMessageObject();
        log.trace("Received message ", clusterStartMessage);

        switch (clusterStartMessage.getClusterStartMessageType()) {
            case CLUSTER_START_SUCCEEDED:
                startRemainingNodes(clusterStartMessage.getSenderId());
                break;
            case CLUSTER_START_FAILED:
                turnOffApplication();
                break;
            case CLUSTER_JOIN_FAILED:
                log.warn("Could not join cluster", clusterStartMessage.getSenderId());
                break;
            case CREATE_WORKER_FAILED:
                log.warn("Could not create worker", clusterStartMessage.getSenderId());
                break;
            default:
                // actually, do nothing. Massage is already logged.
        }
    }

    private void startRemainingNodes(NodeId senderId) {
        nodes()
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(senderId))
                .forEach(entry -> {
                    ClusterStartMessageType msgType;
                    if (entry.getValue().getRoles().contains(ClusterProps.CLUSTER_MEMBER)) {
                        msgType = ClusterStartMessageType.JOIN_CLUSTER;
                    } else {
                        msgType = ClusterStartMessageType.CREATE_WORKER;
                    }

                    topic(entry.getKey())
                            .publish(ClusterStartMessage.builder()
                                    .withClusterStartMessageType(msgType)
                                    .withSenderId(nodeId)
                                    .build());
                });

        management().put(StartupProps.STATUS, StartupState.WORKING);
    }

    private void turnOffApplication() {
        log.info("Cluster start failed. Closing nodes.");

        nodes()
                .keySet()
                .stream()
                .forEach(id ->
                        topic(id).publish(ClusterStartMessage.builder()
                                .withSenderId(nodeId)
                                .withClusterStartMessageType(ClusterStartMessageType.EXIT_APPLICATION)
                                .build())
                );

        management().put(StartupProps.STATUS, StartupState.FINISHED);
        System.exit(1);
    }

}
