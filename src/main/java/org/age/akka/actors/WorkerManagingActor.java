package org.age.akka.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.helper.AkkaConfigConstants;
import org.age.akka.helper.PathHelper;
import org.age.akka.messages.ListNodes;
import org.age.akka.messages.RegisterWorker;

public class WorkerManagingActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    Cluster cluster = Cluster.get(getContext().system());

    public WorkerManagingActor() {
        receive(ReceiveBuilder
                .match(ClusterEvent.MemberUp.class, memberUp -> {
                    log.info("Member up: " + memberUp.member());
                    if (memberUp.member().hasRole(AkkaConfigConstants.CLUSTER_MEMBER_ROLE)) {
                        Address address = memberUp.member().address();
                        String path = PathHelper.createPath(address.hostPort(), AkkaConfigConstants.CLUSTER_MANAGING_AGENT_NAME);
                        ActorSelection actorSelection = context().actorSelection(path);
                        actorSelection.tell(new RegisterWorker(), self());
                    }
                })
                .match(ListNodes.class, e -> {
                    log.info("Worker creation confirmed");
                })
                .build());

    }


    @Override
    public void preStart() throws Exception {
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
    }
}
