package org.age.akka;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.age.akka.start.ClusterManagerStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class ClusterManager {

    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);
    private static final int MIN_CLIENTS_NR = 3;
    private static final int MAX_ATTEMPTS = 3;
    private ClusterManagerStarter clusterManagerStarter;
    private HazelcastInstance hazelcastInstance;

    public ClusterManager() {
        ApplicationContext context = new ClassPathXmlApplicationContext("akka/config/app.cfg.xml");
        clusterManagerStarter = context.getBean(ClusterManagerStarter.class);
        hazelcastInstance = context.getBean(HazelcastInstance.class);
    }

    public static void main(String[] args) throws InterruptedException {
        ClusterManager manager = new ClusterManager();

        manager.waitForSufficientClients();
        if (manager.startCluster()) {
            manager.startTask();
        }
    }

    //temporary method
    private void waitForSufficientClients() throws InterruptedException {
        IMap<Object, Object> nodesMap = hazelcastInstance.getMap("nodesMap");

        while (nodesMap.size() < MIN_CLIENTS_NR) {
            TimeUnit.MILLISECONDS.sleep(500);
            log.info("Not sufficient number of clients. Currently ", nodesMap.size(), " waiting unit ", MIN_CLIENTS_NR, " present");
        }
    }

    private boolean startCluster() throws InterruptedException {
        int clusterStartAttempt = 0;
        while (clusterStartAttempt++ < MAX_ATTEMPTS) {
            if (clusterManagerStarter.startCluster()) {
                return true;
            }
            TimeUnit.MILLISECONDS.wait(1500);
            log.info("Cluster not yet started. Attempt ", clusterStartAttempt, " of ", MAX_ATTEMPTS, ". Waiting 1500 ms for next try");
        }
        return false;
    }

    private void startTask() {
    }
}
