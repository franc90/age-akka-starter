package org.age.akka.start;

import org.age.akka.start.initialization.ManagerInitializer;
import org.age.akka.start.utils.ClusterDataHolder;
import org.age.akka.start.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ClusterManagerStarter extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterManagerStarter.class);

    @Inject
    private ManagerInitializer managerInitializer;

    @Inject
    private ClusterDataHolder clusterDataHolder;

    private String nodeId;

    public boolean startCluster() {
        nodeId = managerInitializer.initialize();

        return false;
    }
}
