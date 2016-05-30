package org.age.akka.start.cluster.initialization;

import org.age.akka.start.cluster.enums.ClusterStatus;
import org.age.akka.start.cluster.enums.ManagementMapProperties;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.message.ClusterStartMessageType;
import org.age.akka.start.common.utils.HazelcastBean;
import org.age.akka.start.common.utils.SleepUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class MasterStarter extends HazelcastBean implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MasterStarter.class);

    private final int minimalNumberOfClients;

    @Inject
    public MasterStarter(@Value("${cluster.minimal.clients:3}") int minimalNumberOfClients) {
        this.minimalNumberOfClients = minimalNumberOfClients;
    }

    @Override
    public void run() {
        ClusterStatus clusterStatus = (ClusterStatus) management().get(ManagementMapProperties.STATUS);
        switch (clusterStatus) {
            case WAITING_FOR_NODES:
                waitForRequiredNodesNumber();
            case INITIALIZING:
                log.info("Initializing akka cluster");
                startClusterCreation();
            case WORKING:
                log.info("Cluster already initialized");
                break;
            case SHUT_DOWN:
                log.info("Cluster is shut down");
                System.exit(0);
        }
    }

    private void waitForRequiredNodesNumber() {
        while (nodes().size() < minimalNumberOfClients) {
            SleepUtils.sleep(500);
            log.info("Waiting for nodes [" + nodes().size() + "/" + minimalNumberOfClients + " ]");
        }
        management().put(ManagementMapProperties.STATUS, ClusterStatus.INITIALIZING);
    }

    private void startClusterCreation() {
        List<String> clusterNodes = selectClusterNodes();
        if (CollectionUtils.isEmpty(clusterNodes)) {
            log.error("No cluster nodes. Never should happen, but who knows?");
            return;
        }

        createAndPublishClusterConfig(clusterNodes);

        String clusterStartNode = clusterNodes.get(0);
        log.trace("Cluster start node: " + clusterStartNode);

        topic(clusterStartNode)
                .publish(ClusterStartMessage.builder()
                        .withClusterStartMessageType(ClusterStartMessageType.START_CLUSTER)
                        .withSenderUUID(myUUID())
                        .build());

        while (management().get(ManagementMapProperties.STATUS) != ClusterStatus.WORKING) {
            log.trace("Waiting for cluster initialization");
            SleepUtils.sleep(250);
        }
    }

    private List<String> selectClusterNodes() {
        int clusterNodesCount = getNodesCount();

        List<String> clusterNodes = new ArrayList<>(clusterNodesCount);
        List<String> allNodes = new ArrayList<>(nodes().keySet());
        Collections.shuffle(allNodes);
        for (int i = 0; i < clusterNodesCount; i++) {
            clusterNodes.add(allNodes.get(i));
        }

        return clusterNodes;
    }

    private int getNodesCount() {
        int allNodes = nodes().size();

        if (allNodes < 1) {
            log.warn("Not enough nodes for starting service. Please provide at least two nodes");
            management().put(ManagementMapProperties.STATUS, ClusterStatus.SHUT_DOWN);
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

    private void createAndPublishClusterConfig(List<String> clusterNodes) {
        ClusterConfigHolder configHolder = ClusterConfigHolder.builder()
                .withClusterNodes(
                        clusterNodes.stream()
                                .map(nodes()::get)
                                .collect(Collectors.toList())
                ).build();


        log.trace("Creating cluster for config " + configHolder);

        management().put(ManagementMapProperties.CLUSTER_CONFIG, configHolder);
    }
}
