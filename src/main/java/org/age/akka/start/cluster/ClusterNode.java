package org.age.akka.start.cluster;

import akka.actor.ActorSystem;
import org.age.akka.core.NodeStarter;
import org.age.akka.start.cluster.akka.AkkaNodeCreator;
import org.age.akka.start.cluster.enums.ClusterStatus;
import org.age.akka.start.cluster.enums.ManagementMapProperties;
import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.utils.ClusterDataHolder;
import org.age.akka.start.common.utils.HazelcastBean;
import org.age.akka.start.common.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ClusterNode extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterNode.class);

    private final NodeStarter nodeStarter;

    private final AkkaNodeCreator akkaNodeCreator;

    private final ClusterDataHolder clusterDataHolder;

    @Inject
    public ClusterNode(NodeStarter nodeStarter, AkkaNodeCreator akkaNodeCreator, ClusterDataHolder clusterDataHolder) {
        this.nodeStarter = nodeStarter;
        this.akkaNodeCreator = akkaNodeCreator;
        this.clusterDataHolder = clusterDataHolder;
    }

    @PostConstruct
    public void init() {
        clusterMembers().add(myUUID());
    }

    public void startWork() {
        initializeCluster();
    }

    private void initializeCluster() {
        AkkaNode currentNode = akkaNodeCreator.createAkkaNode();

        while (true) {
            ClusterStatus status = (ClusterStatus) management().getOrDefault(ManagementMapProperties.STATUS, ClusterStatus.INITIALIZING);

            if (status == ClusterStatus.INITIALIZING || status == ClusterStatus.WAITING_FOR_NODES) {
                log.trace("Waiting for cluster initialization");
            } else if (status == ClusterStatus.WORKING) {
                if (!clusterDataHolder.getCreatingWorker()) {
                    log.info("Creating worker");
                    ClusterConfigHolder configHolder = (ClusterConfigHolder) management().get(ManagementMapProperties.CLUSTER_CONFIG);

                    ActorSystem actorSystem = nodeStarter.createWorker(ClusterConfigHolder.builder()
                            .withCurrentNode(currentNode)
                            .withClusterNodes(configHolder.getClusterNodes())
                            .build());

                    clusterDataHolder.setActorSystem(actorSystem);
                    return;
                } else {
                    log.info("Cluster created");
                    return;
                }
            } else if (status == ClusterStatus.SHUT_DOWN) {
                log.trace("Cluster shut down");
                System.exit(0);
            }
            SleepUtils.sleep(250);
        }
    }


}
