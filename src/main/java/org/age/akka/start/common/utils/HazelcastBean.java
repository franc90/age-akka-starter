package org.age.akka.start.common.utils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.message.ClusterStartMessage;

import javax.inject.Inject;

public abstract class HazelcastBean {

    @Inject
    private HazelcastInstance hazelcastInstance;

    public String getNodeUUID() {
        return hazelcastInstance.getLocalEndpoint().getUuid();
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public IMap<String, AkkaNode> nodes() {
        return hazelcastInstance.getMap("nodes");
    }

    public IMap<String, Object> management() {
        return hazelcastInstance.getMap("management");
    }

    public ITopic<ClusterStartMessage> topic(String nodeId) {
        return hazelcastInstance.getTopic(nodeId.toString());
    }

}
