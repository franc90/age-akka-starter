package org.age.akka.start.cluster.manager.initialization;

import com.hazelcast.core.MessageListener;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named("org.age.akka.start.cluster.manager.initialization.ClusterManagerInitializer")
public class ClusterManagerInitializer extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterManagerInitializer.class);

    @Inject
    @Named("org.age.akka.start.startup.manager.message.listener.ClusterManagerStartMessageListener")
    private MessageListener<ClusterStartMessage> messageListener;

    public void initialize() {
        log.trace("initializing manager");

        topic(getNodeUUID()).addMessageListener(messageListener);
    }

}
