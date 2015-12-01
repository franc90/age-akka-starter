package org.age.akka.core.actors.proxy;

import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.core.actors.proxy.key.WorkerMemberKey;
import org.age.akka.core.helper.AkkaConfigConstants;
import org.age.akka.core.helper.PathCreator;

public class ClusterProxyActor extends AbstractProxyActor<WorkerMemberKey> {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private Cluster cluster = Cluster.get(getContext().system());

    public ClusterProxyActor() {
        receive(addClusterEventsSupport()
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
        String path = PathCreator.createPath(memberAddress.hostPort(), AkkaConfigConstants.WORKER_PROXY_AGENT_NAME);

        return WorkerMemberKey
                .builder()
                .withPath(path)
                .build();
    }
}

