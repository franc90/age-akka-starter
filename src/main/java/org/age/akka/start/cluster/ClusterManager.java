package org.age.akka.start.cluster;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.age.akka.start.cluster.manager.ClusterManagerStarter;
import org.age.akka.start.cluster.manager.members.bus.MemberUpdatedMessage;
import org.age.akka.start.common.utils.HazelcastBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ClusterManager extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);

    @Inject
    private ClusterManagerStarter clusterManagerStarter;

    @Inject
    private EventBus eventBus;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    @Subscribe
    private void updateUsers(MemberUpdatedMessage memberUpdated) {
        log.info("Received membership update event ", memberUpdated);
        if (memberUpdated.getState() == MemberUpdatedMessage.State.REMOVED) {
            nodes().remove(memberUpdated.getUuid());
        }
    }

    public void startWork() throws InterruptedException {
        clusterManagerStarter.startAkkaCluster();
        log.info("Cluster started. Performing scheduled tasks");
        System.out.println("\n\n\n\nSTARTING A TASK\n\n\n\n");
    }

}
