package org.age.akka.start.cluster.lifecycle;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hazelcast.core.MessageListener;
import org.age.akka.start.cluster.enums.ClusterStatus;
import org.age.akka.start.cluster.enums.ManagementMapProperties;
import org.age.akka.start.cluster.initialization.MasterStarter;
import org.age.akka.start.cluster.lifecycle.membership.MemberUpdatedMessage;
import org.age.akka.start.common.message.ClusterStartMessage;
import org.age.akka.start.common.utils.HazelcastBean;
import org.age.akka.start.common.utils.MasterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;

@Named
public class LifecycleMonitor extends HazelcastBean {

    private static final Logger log = LoggerFactory.getLogger(LifecycleMonitor.class);

    private final MessageListener<ClusterStartMessage> masterListener;

    private final MasterUtils masterUtils;

    private final MasterStarter masterStarter;

    private final EventBus eventBus;

    @Inject
    public LifecycleMonitor(@Named("masterListener") MessageListener<ClusterStartMessage> masterListener,
                            MasterUtils masterUtils, MasterStarter masterStarter, EventBus eventBus) {
        this.masterListener = masterListener;
        this.masterUtils = masterUtils;
        this.masterStarter = masterStarter;
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void init() {
        eventBus.register(this);
        initClusterMaster();
    }

    private void initClusterMaster() {
        management().putIfAbsent(ManagementMapProperties.STATUS, ClusterStatus.WAITING_FOR_NODES);
        topic("master").addMessageListener(masterListener);
        String myUUID = myUUID();
        String masterUUID = masterUtils.getMasterUUID().alterAndGet(oldVal -> {
            if (oldVal == null) {
                return myUUID;
            }
            return oldVal;
        });

        if (myUUID.equals(masterUUID)) {
            log.info("I am master");
            masterUtils.setMaster();
            startMaster();
        } else {
            log.info("I am slave, " + masterUUID + " is master.");
        }
    }

    @Subscribe
    public void updateUsers(MemberUpdatedMessage memberUpdated) {
        log.info("Received membership update event ", memberUpdated);
        if (memberUpdated.getState() == MemberUpdatedMessage.State.REMOVED) {
            String removedUUID = memberUpdated.getUuid();
            nodes().remove(removedUUID);
            clusterMembers().remove(removedUUID);
            if (removedUUID.equals(masterUtils.getMasterUUID().get())) {
                updateClusterMaster();
            }
        }
    }

    private void updateClusterMaster() {
        String myUUID = myUUID();
        HashSet<String> allUUIDs = new HashSet<>(clusterMembers());

        int minUUIDHashCode = allUUIDs.stream().mapToInt(String::hashCode).min().orElse(-1);
        int myUUIDHashCode = myUUID.hashCode();

        if (myUUIDHashCode <= minUUIDHashCode) {
            masterUtils.getMasterUUID().set(myUUID);
            masterUtils.setMaster();
            startMaster();
            log.info("I am master");
        } else {
            log.info("I am slave");
        }
    }

    private void startMaster() {
        new Thread(masterStarter).start();
    }

}
