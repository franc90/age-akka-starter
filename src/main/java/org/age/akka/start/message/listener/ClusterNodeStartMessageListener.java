package org.age.akka.start.message.listener;

import com.hazelcast.core.Message;
import org.age.akka.start.message.ClusterStartMessage;
import org.age.akka.start.message.ClusterStartMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@Named("nodeStartMessageListener")
public class ClusterNodeStartMessageListener extends AbstractMessageListener {

    private static final Logger log = LoggerFactory.getLogger(ClusterNodeStartMessageListener.class);

    @Override
    public void onMessage(Message<ClusterStartMessage> message) {
        ClusterStartMessage clusterStartMessage = message.getMessageObject();
        switch (clusterStartMessage.getClusterStartMessageType()) {
            case START_CLUSTER:
                startCluster(clusterStartMessage.getSenderId());
                break;
            case JOIN_CLUSTER:
                joinCluster();
                break;
            default:
                log.warn("Wrong message type: ");
        }
    }

    private void startCluster(String senderId) {
        // TODO: create cluster

        // if created
        topic(senderId).publish(ClusterStartMessage.builder()
                .withSenderId(nodeId)
                .withClusterStartMessageType(ClusterStartMessageType.CLUSTER_START_SUCCEEDED)
                .build());
        // update dataHolder

        // else
        topic(senderId).publish(ClusterStartMessage.builder()
                .withSenderId(nodeId)
                .withClusterStartMessageType(ClusterStartMessageType.CLUSTER_START_FAILED)
                .build());
    }

    private void joinCluster() {
        // TODO: join cluster
        // update dataHolder
    }

}
