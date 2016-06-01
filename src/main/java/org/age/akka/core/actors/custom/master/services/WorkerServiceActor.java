package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.Identify;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import javaslang.Tuple;
import javaslang.Tuple2;
import org.age.akka.core.actors.custom.worker.NodeId;
import org.age.akka.core.actors.messages.Message;
import org.age.akka.core.actors.messages.task.UpdateTaskStateRequest;
import org.age.akka.core.actors.messages.worker.lifecycle.AddWorkerFailedResponse;
import org.age.akka.core.actors.messages.worker.lifecycle.AddWorkerRequest;
import org.age.akka.core.actors.messages.worker.lifecycle.AddWorkerSucceededResponse;
import org.age.akka.core.actors.messages.worker.lifecycle.RemoveWorkerRequest;
import org.age.akka.core.actors.messages.worker.nodes.CurrentWorkerNodesResponse;
import org.age.akka.core.actors.messages.worker.nodes.GetCurrentWorkerNodesRequest;
import org.age.akka.core.actors.messages.worker.nodes.InterruptWorkerRequest;
import org.age.akka.core.actors.messages.worker.nodes.StartWorkerTaskRequest;
import org.age.akka.core.actors.messages.worker.topology.UpdateWorkerTopologiesRequest;
import org.age.akka.core.actors.messages.worker.topology.UpdateWorkerTopologiesResponse;
import org.age.akka.core.actors.messages.worker.topology.UpdateWorkerTopologyRequest;
import org.age.akka.start.common.utils.SleepUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkerServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private static final int MAX_CONNECTION_ATTEMPTS = 20;

    private final Map<NodeId, ActorRef> workerNodes = new HashMap<>();

    private final Map<String, Tuple2<Integer, NodeId>> workerConnectionAttempts = new HashMap<>();

    public WorkerServiceActor() {
        receive(ReceiveBuilder
                .match(AddWorkerRequest.class, this::processAddWorkerRequest)
                .match(ActorIdentity.class, this::processWorkerIdentityResponse)
                .match(RemoveWorkerRequest.class, this::processRemoveWorkerRequest)
                .match(UpdateTaskStateRequest.class, this::processUpdateTaskStateRequest)
                .match(GetCurrentWorkerNodesRequest.class, this::processGetCurrentNodesRequest)
                .match(UpdateWorkerTopologiesRequest.class, this::processUpdateWorkerTopologiesRequest)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    private void processAddWorkerRequest(AddWorkerRequest request) throws Exception {
        log.debug("add worker");
        Address workerAddress = request.getWorkerAddress();
        NodeId id = NodeId.fromAddress(workerAddress);
        log.debug("Look for worker node at {}", id);

        String path = "akka.tcp://age3@" + id.getHostname() + ":" + id.getPort() + "/user/" + id.getName();
        workerConnectionAttempts.putIfAbsent(path, Tuple.of(0, id));

        askWorkerForIdentity(path);
    }

    private void processWorkerIdentityResponse(ActorIdentity workerIdentity) {
        ActorRef workerRef = workerIdentity.getRef();
        String workerPath = (String) workerIdentity.correlationId();

        Tuple2<Integer, NodeId> workerConnectionAttemptData = workerConnectionAttempts.get(workerPath);
        Integer connectionCount = workerConnectionAttemptData._1;
        if (workerRef == null) {
            if (connectionCount < MAX_CONNECTION_ATTEMPTS) {
                log.debug("Unsuccessful connection try #{} to worker {}", connectionCount, workerPath);
                workerConnectionAttempts.put(workerPath, Tuple.of(connectionCount + 1, workerConnectionAttemptData._2));

                //todo replace with scheduler
                SleepUtils.sleep(200L);

                askWorkerForIdentity(workerPath);
            } else {
                log.warning("Cannot find actor at path {}.", workerPath);
                workerConnectionAttempts.remove(workerPath);
                sendToParent(new AddWorkerFailedResponse(workerConnectionAttemptData._2));
            }
        } else {
            workerConnectionAttempts.remove(workerPath);
            NodeId workerNodeId = workerConnectionAttemptData._2;
            workerNodes.put(workerNodeId, workerRef);
            sendToParent(new AddWorkerSucceededResponse(workerNodeId));
        }
    }

    private void askWorkerForIdentity(String path) {
        ActorSelection actorSelection = getContext().actorSelection(path);
        actorSelection.tell(new Identify(path), self());
    }

    private void processRemoveWorkerRequest(RemoveWorkerRequest request) {
        log.debug("remove worker node");
        NodeId id = NodeId.fromAddress(request.getWorkerAddress());

        log.debug("Stop and remove worker node {}", id);
        ActorRef workerRef = workerNodes.remove(id);
        context().stop(workerRef);
    }

    private void processUpdateTaskStateRequest(UpdateTaskStateRequest request) {
        log.debug("sending {} to {} member nodes", request.getType(), workerNodes.size());
        switch (request.getType()) {
            case START:
            case RESUME:
                workerNodes.values().forEach(node -> node.tell(new StartWorkerTaskRequest(), self()));
                break;
            case PAUSE:
                workerNodes.values().forEach(node -> node.tell(new InterruptWorkerRequest(true, false), self()));
                break;
            case CANCEL:
                workerNodes.values().forEach(context()::stop);
                break;
        }
    }

    private void processGetCurrentNodesRequest(GetCurrentWorkerNodesRequest request) {
        log.debug("get current cluster nodes");
        Set<NodeId> nodeIds = new HashSet<>(workerNodes.keySet());
        sender().tell(new CurrentWorkerNodesResponse(nodeIds), self());
    }


    private void processUpdateWorkerTopologiesRequest(UpdateWorkerTopologiesRequest request) {
        log.debug("update worker topologies");
        workerNodes.values().forEach(node -> node.tell(new UpdateWorkerTopologyRequest(request.getTopology()), self()));
        sendToParent(new UpdateWorkerTopologiesResponse());
    }

    private void sendToParent(Message msg) {
        context().parent().tell(msg, self());
    }
}
