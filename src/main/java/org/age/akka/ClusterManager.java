package org.age.akka;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.age.akka.start.cluster.manager.ClusterManagerStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ClusterManager {

    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);

    private final int minimalNumberOfClients;

    @Autowired
    private ClusterManagerStarter clusterManagerStarter;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    public ClusterManager(int minimalNumberOfClients) {
        log.info("Starting cluster manager");
        this.minimalNumberOfClients = minimalNumberOfClients;
        log.info("Cluster manager created");
    }


    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationSpringConfiguration.class);
        ClusterManager manager = context.getBean("clusterManager", ClusterManager.class);

        manager.startWork();
    }

    private void startWork() throws InterruptedException {
        waitForSufficientClients();
        startCluster();
        startTask();
    }

    private void waitForSufficientClients() throws InterruptedException {
        IMap<Object, Object> nodesMap = hazelcastInstance.getMap("nodes");

        while (nodesMap.size() < minimalNumberOfClients) {
            TimeUnit.MILLISECONDS.sleep(500);
            log.info("Not sufficient number of clients. [" + nodesMap.size() + " of " + minimalNumberOfClients + " ]");
        }
    }

    private void startCluster() throws InterruptedException {
        clusterManagerStarter.startCluster();
    }

    private void startTask() {
    }
}
