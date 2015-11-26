package org.age.akka.start.initialization;

import org.age.akka.start.data.AkkaNode;
import org.age.akka.start.enums.ClusterProps;
import org.age.akka.start.exception.NodeInitializationException;
import org.age.akka.start.message.listener.AbstractMessageListener;
import org.age.akka.start.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.Map;

@Named
public class NodeInitializer extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(NodeInitializer.class);

    private static final int START_RETRIES = 10;

    @Inject
    private HostnameFinder hostnameFinder;

    @Inject
    private PortFinder portFinder;

    @Inject
    @Named("nodeStartMessageListener")
    private AbstractMessageListener messageListener;


    /**
     * @return nodeId
     * @throws UnknownHostException
     */
    public String initialize() throws UnknownHostException {
        log.trace("initializing node");
        String hostname = hostnameFinder.getHostname();

        int startTry = 0;
        while (startTry++ < START_RETRIES) {
            int port = portFinder.getPort();

            long conflictingHosts = nodes()
                    .entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .filter(e -> e.getActorSystemName().equals(ClusterProps.ACTOR_SYSTEM_NAME))
                    .filter(e -> e.getHostname().equals(hostname))
                    .filter(e -> e.getPort() == port)
                    .count();

            if (conflictingHosts == 0) {
                AkkaNode akkaNode = AkkaNode.builder()
                        .withActorSystemName(ClusterProps.ACTOR_SYSTEM_NAME)
                        .withHostname(hostname)
                        .withPort(port)
                        .build();

                String nodeId = akkaNode.getNodeId();
                nodes().put(nodeId, akkaNode);

                messageListener.setNodeId(nodeId);
                topic(nodeId).addMessageListener(messageListener);

                log.info("Node initialization succeeded. NodeId = ", nodeId, akkaNode);
                return nodeId;
            }

            log.info("Node initialization try ", startTry, "of ", START_RETRIES, " failed.");
        }


        throw new NodeInitializationException("Could not find free port for node");
    }

}
