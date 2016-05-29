package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.cluster.Cluster;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.messages.lifecycle.LifecycleMsg;

import java.util.Optional;

import static akka.cluster.ClusterEvent.*;
import static org.age.akka.core.actors.messages.lifecycle.LifecycleMsg.Type.ADD;
import static org.age.akka.core.actors.messages.lifecycle.LifecycleMsg.Type.REMOVE;

public class LifecycleServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    public LifecycleServiceActor() {
        receive(ReceiveBuilder
                .match(MemberUp.class, memberUp -> memberUp(memberUp.member()))
                .match(ReachableMember.class, reachableMember -> reachableMember(reachableMember.member()))
                .match(MemberExited.class, memberExited -> memberExited(memberExited.member()))
                .match(MemberRemoved.class, memberRemoved -> memberRemoved(memberRemoved.member()))
                .match(UnreachableMember.class, unreachableMember -> unreachableMember(unreachableMember.member()))
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    public void preStart() throws Exception {

        cluster.subscribe(self(), initialStateAsEvents(), MemberEvent.class, ReachableMember.class,
                MemberExited.class, MemberRemoved.class, UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
    }

    private void reachableMember(Member member) {
        log.info("reachable member");
        memberUp(member);
    }

    private void memberUp(Member member) {
        context().parent().tell(new LifecycleMsg(ADD, member.address()), self());
    }

    private void memberExited(Member member) {
        log.info("member exited");
        unreachableMember(member);
    }

    private void memberRemoved(Member member) {
        log.info("member removed");
        unreachableMember(member);
    }

    private void unreachableMember(Member member) {
        context().parent().tell(new LifecycleMsg(REMOVE, member.address()), self());
    }

    private Optional<ActorSelection> findWorkerService() {
        ActorSelection workerService = context().actorSelection("../workerService");
        if (workerService == null) {
            log.warning("No worker service found");
            return Optional.empty();
        }
        return Optional.of(workerService);
    }

}
