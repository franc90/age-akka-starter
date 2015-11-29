package org.age.akka.start.startup.manager.initialization;

import org.age.akka.start.common.data.NodeId;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.age.akka.start.common.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;

@Component
public class ClusterManagerInitializer extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterManagerInitializer.class);

    @Inject
    @Named("clusterStartMessageListener")
    private AbstractMessageListener messageListener;

    @Inject
    private NodeId nodeId;

    public void initialize() {
        log.trace("initializing manager");

        topic(nodeId).addMessageListener(messageListener);
    }

}
