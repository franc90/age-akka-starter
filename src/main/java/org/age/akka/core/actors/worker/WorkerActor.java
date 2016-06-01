package org.age.akka.core.actors.worker;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.worker.task.TaskActor;
import org.age.akka.core.helper.PathCreator;
import org.age.akka.core.messages.node.lifecycle.TaskInterruptedRequest;
import org.age.akka.core.messages.node.lifecycle.TaskInterruptedResponse;
import org.age.akka.core.messages.node.messaging.BroadcastRequest;
import org.age.akka.core.messages.node.messaging.TaskMessage;
import org.age.akka.core.messages.worker.nodes.InterruptWorkerRequest;
import org.age.akka.core.messages.worker.nodes.StartWorkerTaskRequest;
import org.age.akka.core.messages.worker.topology.UpdateWorkerTopologyRequest;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class WorkerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Class<TaskActor> taskClass;

    private ActorRef taskRef;

    private DirectedGraph<NodeId, DefaultEdge> topology;

    private boolean paused;

    private boolean cancelled;

    private boolean started;

    public WorkerActor(Class<TaskActor> taskClass) {
        this.taskClass = taskClass;
        receive(ReceiveBuilder
                .match(StartWorkerTaskRequest.class, this::processStartTaskRequest)
                .match(UpdateWorkerTopologyRequest.class, this::processUpdateWorkerTopologyRequest)
                .match(InterruptWorkerRequest.class, this::processInterruptWorkerRequest)
                .match(TaskInterruptedRequest.class, this::processTaskInterruptedRequest)
                .match(BroadcastRequest.class, this::processBroadcastRequest)
                .match(TaskMessage.class, this::processTaskMessage)
                .matchAny((m -> log.warning("unexpected message {}", m)))
                .build());
    }

    @Override
    public void preStart() throws Exception {
        taskRef = context().actorOf(Props.create(taskClass), "task");
        context().watch(taskRef);
        log.debug("Joined this cluster because I make poor life choices.");
    }

    @Override
    public void postStop() throws Exception {
        context().unwatch(taskRef);
        context().stop(taskRef);
    }

    private void processStartTaskRequest(StartWorkerTaskRequest request) {
        if (started) {
            if (cancelled) {
                log.debug("taskRef cancelled - cannot resume");
                return;
            }
            if (!paused) {
                log.debug("taskRef not paused - cannot resume");
                return;
            }
        }
        log.debug("start taskRef message - or resume [paused={}]", paused);
        taskRef.tell(new StartWorkerTaskRequest(), self());
        paused = false;
        started = true;
    }

    private void processUpdateWorkerTopologyRequest(UpdateWorkerTopologyRequest request) {
        log.debug("Node received updated topology: {}", topology);
        topology = request.getTopology();
    }

    private void processInterruptWorkerRequest(InterruptWorkerRequest request) {
        log.debug("InterruptedTaskMsg received: {}", request);
        cancelled = request.isCancel();
        paused = request.isPause();
    }

    private void processTaskInterruptedRequest(TaskInterruptedRequest request) {
        log.debug("return current interruption state");
        sender().tell(new TaskInterruptedResponse(paused, cancelled), self());
    }

    @SuppressWarnings("unchecked")
    private void processBroadcastRequest(BroadcastRequest request) {
        log.debug("Sending processBroadcastRequest message {} ", request);
        topology.vertexSet().stream().forEach(id -> {
            TaskMessage msg = new TaskMessage(request.getContent());
            sendTaskMessage(id, msg);
        });
    }

    private void sendTaskMessage(NodeId id, TaskMessage msg) {
        log.debug("Sending {} to {}", msg, id);
        String path = PathCreator.createPath(id.getHostname(), id.getPort(), id.getName());
        ActorSelection actorSelection = context().actorSelection(path);
        actorSelection.tell(msg, self());
    }

    private void processTaskMessage(TaskMessage msg) {
        log.debug("Received processBroadcastRequest message {}.", msg);
        taskRef.tell(new TaskMessage<>(msg.getContent()), self());
    }

}
