package org.age.akka.start.common.utils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.message.ClusterStartMessage;

import javax.inject.Inject;

public abstract class HazelcastBean {

    @Inject
    private HazelcastInstance hazelcastInstance;

    protected String myUUID() {
        return hazelcastInstance.getLocalEndpoint().getUuid();
    }

    protected HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    protected ISet<String> clusterMembers() {
        return hazelcastInstance.getSet("clusterMembers");
    }

    protected IMap<String, AkkaNode> nodes() {
        return hazelcastInstance.getMap("nodes");
    }

    protected IMap<String, Object> management() {
        return hazelcastInstance.getMap("management");
    }

    protected ITopic<ClusterStartMessage> topic(String nodeId) {
        return hazelcastInstance.getTopic(nodeId.toString());
    }

}
