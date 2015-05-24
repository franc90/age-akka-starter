package org.age.akka.actors.proxy;

import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.actors.proxy.key.WorkerMemberKey;
import org.age.akka.helper.AkkaConfigConstants;
import org.age.akka.helper.PathHelper;
import org.age.akka.messages.ListNodes;

public class ClusterProxyActor extends AbstractProxyActor<WorkerMemberKey> {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private Cluster cluster = Cluster.get(getContext().system());

    public ClusterProxyActor() {
        receive(addClusterEventsSupport()
                .match(ListNodes.class, e -> {
                    log.info("Listing workers: ");
                    members
                            .keySet()
                            .stream()
                            .map(WorkerMemberKey::toString)
                            .forEach(log::info);
                })
                .matchAny(e -> log.info("GOT: " + e))
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

    @Override
    protected String getMemberRole() {
        return AkkaConfigConstants.CLUSTER_WORKER_ROLE;
    }

    @Override
    protected WorkerMemberKey generateKey(Address memberAddress) {
        String path = PathHelper.createPath(memberAddress.hostPort(), AkkaConfigConstants.WORKER_PROXY_AGENT_NAME);

        return WorkerMemberKey
                .builder()
                .withPath(path)
                .build();
    }
}

