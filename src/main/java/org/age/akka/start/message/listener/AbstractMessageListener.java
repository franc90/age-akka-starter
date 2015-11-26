package org.age.akka.start.message.listener;

import com.hazelcast.core.MessageListener;
import org.age.akka.start.message.ClusterStartMessage;
import org.age.akka.start.utils.ClusterDataHolder;
import org.age.akka.start.utils.HazelcastBean;

import javax.inject.Inject;

public abstract class AbstractMessageListener extends HazelcastBean implements MessageListener<ClusterStartMessage> {

    protected String nodeId;

    @Inject
    protected ClusterDataHolder dataHolder;

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
