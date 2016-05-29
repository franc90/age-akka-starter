package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.topology.NewTopologyMsg;
import org.age.akka.core.actors.messages.topology.ProcessNewTopologyMsg;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableDirectedGraph;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Iterables.getLast;

public class TopologyProcessorActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    public TopologyProcessorActor() {
        receive(ReceiveBuilder
                .match(ProcessNewTopologyMsg.class, this::processTopology)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    private void processTopology(ProcessNewTopologyMsg msg) {
        log.info("process new topology with nodes " + msg.getNodeIds().size());
        Set<NodeId> nodeIds = msg.getNodeIds();

        DefaultDirectedGraph<NodeId, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodeIds.forEach(graph::addVertex);

        List<NodeId> sortedIds = nodeIds.stream()
                .sorted()
                .collect(Collectors.toList());

        sortedIds.stream().reduce(getLast(sortedIds), (nodeIdentity1, nodeIdentity2) -> {
            graph.addEdge(nodeIdentity1, nodeIdentity2);
            return nodeIdentity2;
        });

        DirectedGraph<NodeId, DefaultEdge> topology = new UnmodifiableDirectedGraph<>(graph);
        log.info("new Graph: " + topology);
        sender().tell(new NewTopologyMsg(topology), self());
    }

}
