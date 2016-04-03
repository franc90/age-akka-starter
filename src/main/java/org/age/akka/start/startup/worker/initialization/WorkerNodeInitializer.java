package org.age.akka.start.startup.worker.initialization;

import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.data.Hostname;
import org.age.akka.start.common.data.NodeId;
import org.age.akka.start.common.data.Port;
import org.age.akka.start.common.enums.ClusterProps;
import org.age.akka.start.common.exception.NodeInitializationException;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.age.akka.start.common.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@Named
public class WorkerNodeInitializer extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(WorkerNodeInitializer.class);

    private static final int START_RETRIES = 10;

    @Inject
    @Named("workerNodeStartMessageListener")
    private AbstractMessageListener messageListener;

    @Inject
    private NodeId nodeId;

    public void initialize() {
        log.trace("initializing node");
        Hostname hostname = new Hostname();
        log.trace("hostname " + hostname);

        int startTry = 0;
        while (startTry++ < START_RETRIES) {
            Port port = new Port();
            log.trace("port " + port);

            long conflictingHosts = nodes()
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .filter(e -> e.getActorSystemName().equals(ClusterProps.ACTOR_SYSTEM_NAME))
                    .filter(e -> e.getHostname().equals(hostname))
                    .filter(e -> e.getPort().equals(port))
                    .count();

            if (conflictingHosts == 0) {
                AkkaNode akkaNode = AkkaNode.builder()
                        .withActorSystemName(ClusterProps.ACTOR_SYSTEM_NAME)
                        .withHostname(hostname)
                        .withPort(port)
                        .build();

                nodes().put(nodeId, akkaNode);
                topic(nodeId).addMessageListener(messageListener);

                log.info("Node initialization succeeded. NodeId = " + nodeId + " " + akkaNode);
                return;
            }

            log.info("Node initialization try " + startTry + "of " + START_RETRIES + " failed.");
        }


        throw new NodeInitializationException("Could not find free port for node");
    }

}
