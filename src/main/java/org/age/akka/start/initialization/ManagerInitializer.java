package org.age.akka.start.initialization;

import org.age.akka.start.message.listener.AbstractMessageListener;
import org.age.akka.start.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

@Component
public class ManagerInitializer extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ManagerInitializer.class);

    @Inject
    @Named("clusterStartMessageListener")
    private AbstractMessageListener messageListener;

    public String initialize() {
        log.trace("initializing manager");
        String nodeId = UUID.randomUUID().toString();

        messageListener.setNodeId(nodeId);
        topic(nodeId).addMessageListener(messageListener);

        return nodeId;
    }

}
