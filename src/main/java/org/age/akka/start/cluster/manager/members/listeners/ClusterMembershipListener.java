package org.age.akka.start.cluster.manager.members.listeners;

import com.google.common.eventbus.EventBus;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import org.age.akka.start.cluster.manager.members.event.MemberUpdated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ClusterMembershipListener implements MembershipListener {

    private static final Logger log = LoggerFactory.getLogger(ClusterMembershipListener.class);

    @Inject
    private EventBus eventBus;

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        String uuid = membershipEvent.getMember().getUuid();
        MemberUpdated memberUpdated = new MemberUpdated(MemberUpdated.State.ADDED, uuid);
        eventBus.post(memberUpdated);
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        String uuid = membershipEvent.getMember().getUuid();
        MemberUpdated memberUpdated = new MemberUpdated(MemberUpdated.State.REMOVED, uuid);
        eventBus.post(memberUpdated);
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
    }

}
