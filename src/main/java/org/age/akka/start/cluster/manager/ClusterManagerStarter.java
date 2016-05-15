package org.age.akka.start.cluster.manager;

import com.hazelcast.core.MessageListener;
import org.age.akka.start.cluster.enums.StartupState;
import org.age.akka.start.cluster.enums.ManagementMapProperties;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.message.ClusterStartMessageType;
import org.age.akka.start.common.utils.HazelcastBean;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

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

    private final int minimalNumberOfClients;

    @Inject
    @Named("org.age.akka.start.startup.manager.message.listener.ClusterManagerStartMessageListener")
    private MessageListener<ClusterStartMessage> messageListener;

    @Inject
    public ClusterManagerStarter(@Value("${cluster.minimal.clients:3}") int minimalNumberOfClients) {
        this.minimalNumberOfClients = minimalNumberOfClients;
    }

    public void startCluster() throws InterruptedException {
        waitForSufficientClients();
        topic(getNodeUUID()).addMessageListener(messageListener);
        management().put(ManagementMapProperties.STATUS, StartupState.INITIALIZE_CLUSTER);

        startClusterCreation();

        while (management().get(ManagementMapProperties.STATUS) != StartupState.CLUSTER_WORKING) {
            log.trace("Waiting for cluster initialization");
            TimeUnit.MILLISECONDS.sleep(250);
        }

        log.info("Cluster initialized");
        management().put(ManagementMapProperties.STATUS, StartupState.CLUSTER_INITIALIZATION_FINISHED);
    }

    private void waitForSufficientClients() throws InterruptedException {
        while (nodes().size() < minimalNumberOfClients) {
            TimeUnit.MILLISECONDS.sleep(500);
            log.info("Not sufficient number of clients. [" + nodes().size() + " of " + minimalNumberOfClients + " ]");
        }
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
                        .withSenderUUID(getNodeUUID())
                        .build());
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

        if (allNodes < 2) {
            log.warn("Not enough nodes for starting service. Please provide at least two nodes");
            management().put(ManagementMapProperties.STATUS, StartupState.CLUSTER_INITIALIZATION_FINISHED);
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
