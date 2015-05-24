package org.age.akka.actors.proxy;

import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.actors.proxy.key.ClusterMemberKey;
import org.age.akka.helper.AkkaConfigConstants;
import org.age.akka.helper.ClusterMemberOrderHelper;
import org.age.akka.helper.PathHelper;
import org.age.akka.messages.ListNodes;

public class WorkerProxyActor extends AbstractProxyActor<ClusterMemberKey> {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private Cluster cluster = Cluster.get(getContext().system());

    public WorkerProxyActor() {
        receive(addClusterEventsSupport()
                .match(ListNodes.class, e -> log.info("Worker creation confirmed"))
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
        return AkkaConfigConstants.CLUSTER_MEMBER_ROLE;
    }

    @Override
    protected ClusterMemberKey generateKey(Address memberAddress) {
        String path = PathHelper.createPath(memberAddress.hostPort(), AkkaConfigConstants.CLUSTER_PROXY_AGENT_NAME);
        int order = ClusterMemberOrderHelper.getOrder(memberAddress);

        return ClusterMemberKey
                .builder()
                .withPath(path)
                .withOrder(order)
                .build();
    }
}
