package org.age.akka.start.cluster.message.listener;

import com.hazelcast.core.Message;
import org.age.akka.start.cluster.enums.ClusterStatus;
import org.age.akka.start.cluster.enums.ManagementMapProperties;
import org.age.akka.start.common.enums.ClusterProps;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.message.ClusterStartMessageType;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.age.akka.start.common.utils.MasterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named("masterListener")
public class MasterStartMessageListener extends AbstractMessageListener {

    private static final Logger log = LoggerFactory.getLogger(MasterStartMessageListener.class);

    private final MasterUtils masterUtils;

    @Inject
    public MasterStartMessageListener(MasterUtils masterUtils) {
        this.masterUtils = masterUtils;
    }

    @Override
    public void onMessage(Message<ClusterStartMessage> message) {
        if (masterUtils.isNotMaster()) {
            log.trace("Received cluster manager message, but node is not cluster master");
            return;
        }

        ClusterStartMessage clusterStartMessage = message.getMessageObject();
        log.trace("Received message ", clusterStartMessage);

        switch (clusterStartMessage.getClusterStartMessageType()) {
            case CLUSTER_START_SUCCEEDED:
                startRemainingNodes(clusterStartMessage.getSenderUUID());
                break;
            case CLUSTER_START_FAILED:
                turnOffApplication();
                break;
            case CLUSTER_JOIN_FAILED:
                log.warn("Could not join cluster", clusterStartMessage.getSenderUUID());
                break;
            case CREATE_WORKER_FAILED:
                log.warn("Could not create worker", clusterStartMessage.getSenderUUID());
                break;
            default:
                // actually, do nothing. Massage is already logged.
        }
    }

    private void startRemainingNodes(String senderUUID) {
        nodes()
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(senderUUID))
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
                                    .withSenderUUID(myUUID())
                                    .build());
                });

        management().put(ManagementMapProperties.STATUS, ClusterStatus.WORKING);
    }

    private void turnOffApplication() {
        log.info("Cluster start failed. Closing nodes.");

        nodes()
                .keySet()
                .stream()
                .forEach(id ->
                        topic(id).publish(ClusterStartMessage.builder()
                                .withSenderUUID(myUUID())
                                .withClusterStartMessageType(ClusterStartMessageType.EXIT_APPLICATION)
                                .build())
                );

        management().put(ManagementMapProperties.STATUS, ClusterStatus.SHUT_DOWN);
        System.exit(1);
    }

}
