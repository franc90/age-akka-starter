package org.age.akka.start.cluster.manager;

import org.age.akka.start.cluster.StartupState;
import org.age.akka.start.cluster.enums.StartupProps;
import org.age.akka.start.cluster.manager.initialization.ClusterManagerInitializer;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.message.ClusterStartMessageType;
import org.age.akka.start.common.utils.ClusterDataHolder;
import org.age.akka.start.common.utils.HazelcastBean;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Named("org.age.akka.start.startup.manager.ClusterManagerStarter")
public class ClusterManagerStarter extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterManagerStarter.class);

    @Inject
    private ClusterManagerInitializer clusterManagerInitializer;

    @Inject
    private ClusterDataHolder clusterDataHolder;

    public void startCluster() throws InterruptedException {
        management().put(StartupProps.STATUS, StartupState.INIT);
        clusterManagerInitializer.initialize();

        startClusterCreation();

        while (management().get(StartupProps.STATUS) != StartupState.WORKING) {
            log.trace("Waiting for cluster initialization");
            TimeUnit.MILLISECONDS.sleep(250);
        }

        doWork();

        log.info("Cluster initialized");
        management().put(StartupProps.STATUS, StartupState.FINISHED);
    }

    private void startClusterCreation() {
        List<String> clusterNodes = selectClusterNodes();
        if (CollectionUtils.isEmpty(clusterNodes)) {
            log.error("No cluster nodes. Never should happen, but who knows?");
            return;
        }

        createClusterConfig(clusterNodes);

        String clusterStartNode = clusterNodes.get(0);
        log.trace("Cluster start node: " + clusterStartNode);

        topic(clusterStartNode)
                .publish(ClusterStartMessage.builder()
                        .withClusterStartMessageType(ClusterStartMessageType.START_CLUSTER)
                        .withSenderUUID(getNodeUUID())
                        .build());
    }

    private List<String> selectClusterNodes() {
        int clusterNodesCount = getNodesCount();

        List<String> clusterNodes = new ArrayList<>();
        List<String> allNodes = new ArrayList<>(nodes().keySet());
        Collections.shuffle(allNodes);
        for (int i = 0; i < clusterNodesCount; i++) {
            clusterNodes.add(allNodes.get(i));
        }

        return clusterNodes;
    }

    private int getNodesCount() {
        int allNodes = nodes().size();

        if (allNodes < 2) {
            log.warn("Not enough nodes for starting service. Please provide at least two nodes");
            management().put(StartupProps.STATUS, StartupState.FINISHED);
            System.exit(1);
        }
        if (allNodes < 4) {
            return 1;
        }
        if (allNodes < 10) {
            return 2;
        }
        if (allNodes < 15) {
            return 3;
        }
        return 5;
    }

    private void createClusterConfig(List<String> clusterNodes) {
        ClusterConfigHolder configHolder = ClusterConfigHolder.builder()
                .withClusterNodes(
                        clusterNodes.stream()
                                .map(clusterUUID -> nodes().get(clusterUUID))
                                .collect(Collectors.toList())
                ).build();


        log.trace("Creating cluster for config " + configHolder);

        management().put(StartupProps.CLUSTER_CONFIG, configHolder);
    }

    private void doWork() {

    }
}
