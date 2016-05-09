package org.age.akka;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.age.akka.start.cluster.manager.ClusterManagerStarter;
import org.age.akka.start.cluster.manager.members.event.MemberUpdated;
import org.age.akka.start.common.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.TimeUnit;

@Named
public class ClusterManager extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);

    private final int minimalNumberOfClients;

    @Inject
    private ClusterManagerStarter clusterManagerStarter;

    @Inject
    private EventBus eventBus;

    @Inject
    public ClusterManager(@Value("${cluster.minimal.clients:3}") int minimalClusterClients) {
        log.info("Starting cluster manager");
        this.minimalNumberOfClients = minimalClusterClients;
        log.info("Cluster manager created");
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationSpringConfiguration.class);
        ClusterManager manager = context.getBean("clusterManager", ClusterManager.class);

        manager.startWork();
    }

    @Subscribe
    private void updateUsers(MemberUpdated memberUpdated) {
        log.info("Received membership update event ", memberUpdated);
        if (memberUpdated.getState() == MemberUpdated.State.REMOVED) {
            nodes().remove(memberUpdated.getUuid());
        }
    }

    private void startWork() throws InterruptedException {
        waitForSufficientClients();
        startCluster();
        startTask();
    }

    private void waitForSufficientClients() throws InterruptedException {
        while (nodes().size() < minimalNumberOfClients) {
            TimeUnit.MILLISECONDS.sleep(500);
            log.info("Not sufficient number of clients. [" + nodes().size() + " of " + minimalNumberOfClients + " ]");
        }
    }

    private void startCluster() throws InterruptedException {
        clusterManagerStarter.startCluster();
    }

    private void startTask() {
        System.out.println("\n\n\n\nSTARTING A TASK\n\n\n\n");
    }
}
