package org.age.akka.start.cluster.akka;

import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.data.Hostname;
import org.age.akka.start.common.data.Port;
import org.age.akka.start.common.enums.ClusterProps;
import org.age.akka.start.common.exception.NodeInitializationException;
import org.age.akka.start.common.message.listener.AbstractMessageListener;
import org.age.akka.start.common.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Set;

@Named
public class AkkaNodeCreator extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(AkkaNodeCreator.class);

    private static final int START_RETRIES = 10;

    private final Set<String> networkInterfaceNames;

    private final AbstractMessageListener slaveListener;

    @Inject
    public AkkaNodeCreator(@Value("#{'${network.interface.names:default}'.split(',')}") Set<String> networkInterfaceNames,
                           @Named("slaveListener") AbstractMessageListener slaveListener) {
        this.networkInterfaceNames = networkInterfaceNames;
        this.slaveListener = slaveListener;
    }

    public AkkaNode createAkkaNode() {
        log.trace("initializing node");
        Hostname hostname = new Hostname(networkInterfaceNames);
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

                nodes().put(myUUID(), akkaNode);
                topic(myUUID()).addMessageListener(slaveListener);

                log.info("Node initialization succeeded. NodeUUID = " + myUUID() + " " + akkaNode);
                return akkaNode;
            }

            log.info("Node initialization try [" + startTry + "/ " + START_RETRIES + "] failed.");
        }

        throw new NodeInitializationException("Could not find free port for node");
    }

}
