package org.age.akka.core.actors.proxy;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import static akka.cluster.ClusterEvent.*;

/**
 * Master manages whole cluster, it is created after startup of first cluster node. Worker nodes are created as its children
 */
public class ClusterMaster extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    @Override
    public void preStart() throws Exception {
        cluster.subscribe(self(), initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
    }

    public ClusterMaster() {
        receive();
    }
}
