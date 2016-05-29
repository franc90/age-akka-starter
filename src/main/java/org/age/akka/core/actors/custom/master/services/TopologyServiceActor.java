package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.topology.NewTopologyMsg;
import org.age.akka.core.actors.messages.topology.ProcessNewTopologyMsg;
import org.age.akka.core.actors.messages.topology.TopologyUpdatedMsg;
import org.age.akka.core.actors.messages.topology.UpdateTopologyMsg;
import org.age.akka.core.actors.messages.worker.GetNodesMsg;
import org.age.akka.core.actors.messages.worker.NodesMsg;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Optional;
import java.util.Set;

public class TopologyServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    private ActorRef topologyProcessor;

    private DirectedGraph<NodeId, DefaultEdge> topology;

    public TopologyServiceActor() {
        receive(ReceiveBuilder
                .match(UpdateTopologyMsg.class, this::updateTopology)
                .match(NodesMsg.class, this::updateTopologyWithCurrentNodes)
                .match(NewTopologyMsg.class, this::newTopology)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    public void preStart() throws Exception {
        topologyProcessor = context().actorOf(Props.create(TopologyProcessorActor.class), "topologyProcessor");
        context().watch(topologyProcessor);
    }

    private void updateTopology(UpdateTopologyMsg msg) {
        log.info("update topology");
        findWorkerService().ifPresent(workerService -> workerService.tell(new GetNodesMsg(), self()));
    }

    private void updateTopologyWithCurrentNodes(NodesMsg msg) {
        log.info("got current nodes, process new topology");
        Set<NodeId> nodeIds = msg.getNodeIdSet();
        topologyProcessor.tell(new ProcessNewTopologyMsg(nodeIds), self());
    }

    private void newTopology(NewTopologyMsg msg) {
        log.info("update topology");
        topology = msg.getTopology();
        context().parent().tell(new TopologyUpdatedMsg(topology), self());

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
