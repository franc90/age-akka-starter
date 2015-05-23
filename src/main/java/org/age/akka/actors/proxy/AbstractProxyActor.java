package org.age.akka.actors.proxy;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.japi.pf.UnitPFBuilder;
import org.age.akka.helper.AkkaConfigConstants;
import org.age.akka.helper.PathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractProxyActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    protected Map<String, ActorSelection> members = new HashMap<>();

    protected UnitPFBuilder<Object> addClusterEventsSupport() {
        return ReceiveBuilder
                .match(ClusterEvent.MemberUp.class, memberUp -> addMember(memberUp.member()))
                .match(ClusterEvent.ReachableMember.class, reachableMember -> addMember(reachableMember.member()))
                .match(ClusterEvent.MemberExited.class, memberExited -> removeMember(memberExited.member()))
                .match(ClusterEvent.MemberRemoved.class, memberRemoved -> removeMember(memberRemoved.member()))
                .match(ClusterEvent.UnreachableMember.class, unreachableMember -> removeMember(unreachableMember.member()));
    }

    protected abstract String getMemberRole();


    private void addMember(Member member) {
        if (!member.hasRole(getMemberRole())) {
            Set<String> roles = member.getRoles();
            String role = roles.isEmpty() ? "" : roles.iterator().next();
            log.info("Member[{}] up: {}", role, member);
            return;
        }

        Address memberAddress = member.address();
        String path = PathHelper.createPath(memberAddress.hostPort(), AkkaConfigConstants.CLUSTER_PROXY_AGENT_NAME);

        if (members.containsKey(path)) {
            return;
        }

        log.info("Member[{}] up and added to members: {}", getMemberRole(), member);
        ActorSelection actorSelection = context().actorSelection(path);
        members.put(path, actorSelection);
    }

    private void removeMember(Member member) {
        if (!member.hasRole(getMemberRole())) {
            Set<String> roles = member.getRoles();
            String role = roles.isEmpty() ? "" : roles.iterator().next();
            log.info("Removing member[{}]: {}", role, member);
            return;
        }

        Address memberAddress = member.address();
        String path = PathHelper.createPath(memberAddress.hostPort(), AkkaConfigConstants.CLUSTER_PROXY_AGENT_NAME);

        if (members.containsKey(path)) {
            return;
        }

        log.info("Removing member[{}]: {}", getMemberRole(), member);
        members.remove(path);
    }
}
