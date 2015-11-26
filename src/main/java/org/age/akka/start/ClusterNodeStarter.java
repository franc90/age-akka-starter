package org.age.akka.start;

import org.age.akka.start.initialization.NodeInitializer;
import org.age.akka.start.utils.ClusterDataHolder;
import org.age.akka.start.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Named
public class ClusterNodeStarter extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterNodeStarter.class);

    @Inject
    private NodeInitializer nodeInitializer;

    @Inject
    private ClusterDataHolder clusterDataHolder;

    private String nodeId;

    public void startWork() throws UnknownHostException, InterruptedException {
        nodeId = nodeInitializer.initialize();

        while (clusterDataHolder.getActorSystem() == null) {
            log.debug("Waiting for cluster initialization");
            TimeUnit.MILLISECONDS.sleep(500);
        }

        log.trace("Cluster initialized");
    }


    public void joinServer() {

    }

    public void waitForExit() {

    }
}
