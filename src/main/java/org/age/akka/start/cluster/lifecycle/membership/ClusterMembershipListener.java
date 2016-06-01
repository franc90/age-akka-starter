package org.age.akka.start.cluster.lifecycle.membership;

import com.google.common.eventbus.EventBus;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ClusterMembershipListener implements MembershipListener {

    @Inject
    private EventBus eventBus;

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        String uuid = membershipEvent.getMember().getUuid();
        MemberUpdatedMessage memberUpdated = new MemberUpdatedMessage(MemberUpdatedMessage.State.ADDED, uuid);
        eventBus.post(memberUpdated);
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        String uuid = membershipEvent.getMember().getUuid();
        MemberUpdatedMessage memberUpdated = new MemberUpdatedMessage(MemberUpdatedMessage.State.REMOVED, uuid);
        eventBus.post(memberUpdated);
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
    }

}
