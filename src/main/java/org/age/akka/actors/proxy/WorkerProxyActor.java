package org.age.akka.actors.proxy;

import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.age.akka.actors.proxy.key.ClusterMemberKey;
import org.age.akka.exceptions.NoMemberInClusterException;
import org.age.akka.helper.AkkaConfigConstants;
import org.age.akka.helper.ClusterMemberOrderHelper;
import org.age.akka.helper.PathHelper;

import java.util.Optional;

public class WorkerProxyActor extends AbstractProxyActor<ClusterMemberKey> {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private Cluster cluster = Cluster.get(getContext().system());
    private ClusterMemberKey currentLeader;

    public WorkerProxyActor() {
        receive(addClusterEventsSupport()
                .matchAny(msg -> log.info("Received not supported message {}", msg))
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
    protected ClusterMemberKey addMember(Member member) {
        ClusterMemberKey newMemberKey = super.addMember(member);
        if (newMemberKey == null) {
            return null;
        }

        checkIfNewLeader(newMemberKey);

        return newMemberKey;
    }

    private void checkIfNewLeader(ClusterMemberKey newMemberKey) {
        if (currentLeader == null || currentLeader.getOrder() > newMemberKey.getOrder()) {
            log.info("New Cluster leader: {}", newMemberKey);
            currentLeader = newMemberKey;
        }
    }

    @Override
    protected ClusterMemberKey removeMember(Member member) {
        ClusterMemberKey clusterMemberKey = super.removeMember(member);

        if (currentLeader.equals(clusterMemberKey)) {
            findNewLeader();
        }

        return clusterMemberKey;
    }

    private void findNewLeader() {
        Optional<ClusterMemberKey> newLeader = members
                .keySet()
                .stream()
                .sorted((k1, k2) -> Integer.compare(k1.getOrder(), k2.getOrder()))
                .findFirst();

        if (!newLeader.isPresent()) {
            throw new NoMemberInClusterException("Could not find new leader. No members in cluster.");
        }

        ClusterMemberKey newMemberKey = newLeader.get();
        log.info("New Cluster leader: {}", newMemberKey);
        currentLeader = newMemberKey;
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
