package org.age.akka.core.actors.master.services;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.messages.lifecycle.LifecycleUpdatedRequest;

import static akka.cluster.ClusterEvent.*;
import static org.age.akka.core.messages.lifecycle.LifecycleUpdatedRequest.Type.ADD;
import static org.age.akka.core.messages.lifecycle.LifecycleUpdatedRequest.Type.REMOVE;

public class LifecycleServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    public LifecycleServiceActor() {
        receive(ReceiveBuilder
                .match(MemberUp.class, memberUp -> addMember(memberUp.member()))
                .match(ReachableMember.class, reachableMember -> addMember(reachableMember.member()))
                .match(MemberExited.class, memberExited -> removeMember(memberExited.member()))
                .match(MemberRemoved.class, memberRemoved -> removeMember(memberRemoved.member()))
                .match(UnreachableMember.class, unreachableMember -> removeMember(unreachableMember.member()))
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    public void preStart() throws Exception {
        cluster.subscribe(self(), initialStateAsEvents(), MemberUp.class, ReachableMember.class,
                MemberExited.class, MemberRemoved.class, UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
    }

    private void addMember(Member member) {
        log.info("inform parent that node should be added {}", member);
        context().parent().tell(new LifecycleUpdatedRequest(ADD, member.address()), self());
    }

    private void removeMember(Member member) {
        log.info("inform parent that node should be removed {}", member);
        context().parent().tell(new LifecycleUpdatedRequest(REMOVE, member.address()), self());
    }

}
