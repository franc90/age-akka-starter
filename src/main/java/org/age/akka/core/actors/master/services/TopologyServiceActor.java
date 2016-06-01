package org.age.akka.core.actors.master.services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.master.services.topology.RingTopologyProcessorActor;
import org.age.akka.core.actors.worker.NodeId;
import org.age.akka.core.messages.topology.NewTopologyRequest;
import org.age.akka.core.messages.topology.NewTopologyResponse;
import org.age.akka.core.messages.topology.TopologyUpdateRequest;
import org.age.akka.core.messages.topology.TopologyUpdateResponse;
import org.age.akka.core.messages.worker.nodes.CurrentWorkerNodesResponse;
import org.age.akka.core.messages.worker.nodes.GetCurrentWorkerNodesRequest;

import java.util.HashSet;
import java.util.Optional;

public class TopologyServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private ActorRef topologyProcessor;

    public TopologyServiceActor() {
        receive(ReceiveBuilder
                .match(TopologyUpdateRequest.class, this::processTopologyUpdateRequest)
                .match(CurrentWorkerNodesResponse.class, this::processCurrentNodesResponse)
                .match(NewTopologyResponse.class, this::processNewTopologyResponse)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    public void preStart() throws Exception {
        topologyProcessor = context().actorOf(Props.create(RingTopologyProcessorActor.class), "topologyProcessor");
        context().watch(topologyProcessor);
    }

    private void processTopologyUpdateRequest(TopologyUpdateRequest request) {
        log.debug("update topology");
        findWorkerService().ifPresent(workerService -> workerService.tell(new GetCurrentWorkerNodesRequest(), self()));
    }

    private void processCurrentNodesResponse(CurrentWorkerNodesResponse response) {
        log.debug("got current nodes, process new topology");
        HashSet<NodeId> nodeIds = response.getNodeIdSet();
        topologyProcessor.tell(new NewTopologyRequest(nodeIds), self());
    }

    private void processNewTopologyResponse(NewTopologyResponse response) {
        log.debug("update topology");
        context().parent().tell(new TopologyUpdateResponse(response.getTopology()), self());

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
