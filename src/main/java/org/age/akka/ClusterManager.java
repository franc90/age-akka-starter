package org.age.akka;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.age.akka.start.cluster.manager.ClusterManagerStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class ClusterManager {

    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);
    private static final int MIN_CLIENTS_NR = 3;
    private ClusterManagerStarter clusterManagerStarter;
    private HazelcastInstance hazelcastInstance;

    private ClusterManager() {
        log.info("Starting cluster manager");
        ApplicationContext context = new ClassPathXmlApplicationContext("akka/config/app.cfg.xml");
        log.info("context loaded");
        clusterManagerStarter = context.getBean(ClusterManagerStarter.class);
        hazelcastInstance = context.getBean(HazelcastInstance.class);
        log.info("Cluster manager created");
    }

    public static void main(String[] args) throws InterruptedException {
        ClusterManager manager = new ClusterManager();

        manager.waitForSufficientClients();
        manager.startCluster();
        manager.startTask();
    }

    private void waitForSufficientClients() throws InterruptedException {
        IMap<Object, Object> nodesMap = hazelcastInstance.getMap("nodes");

        while (nodesMap.size() < MIN_CLIENTS_NR) {
            TimeUnit.MILLISECONDS.sleep(500);
            log.info("Not sufficient number of clients. [" + nodesMap.size() + " of " + MIN_CLIENTS_NR + " ]");
        }
    }

    private void startCluster() throws InterruptedException {
        clusterManagerStarter.startCluster();
    }

    private void startTask() {
    }
}
