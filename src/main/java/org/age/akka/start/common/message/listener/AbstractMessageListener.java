package org.age.akka.start.common.message.listener;

import com.hazelcast.core.MessageListener;
import org.age.akka.start.common.data.NodeId;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.utils.ClusterDataHolder;
import org.age.akka.start.common.utils.HazelcastBean;

import javax.inject.Inject;

public abstract class AbstractMessageListener extends HazelcastBean implements MessageListener<ClusterStartMessage> {

    @Inject
    protected NodeId nodeId;

    @Inject
    protected ClusterDataHolder dataHolder;

}
