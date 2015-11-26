package org.age.akka.start.message.listener;

import com.hazelcast.core.Message;
import org.age.akka.start.message.ClusterStartMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@Named("clusterStartMessageListener")
public class ClusterManagerStartMessageListener extends AbstractMessageListener {

    private static final Logger log = LoggerFactory.getLogger(ClusterManagerStartMessageListener.class);

    @Override
    public void onMessage(Message<ClusterStartMessage> message) {
        // TODO

        // CLUSTER_START_SUCCEEDED -> start remaining cluster and worker nodes
        // CLUSTER_START_FAILED -> exit this shit

    }

}
