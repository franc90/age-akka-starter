package org.age.akka.core.actors.custom.worker;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.custom.worker.task.SimpleBroadcastCommunicationTaskActor;
import org.age.akka.core.actors.messages.node.InterruptTaskMsg;
import org.age.akka.core.actors.messages.node.IsTaskInterruptedMsg;
import org.age.akka.core.actors.messages.node.StartTaskMsg;
import org.age.akka.core.actors.messages.node.TaskInterruptedResponseMsg;
import org.age.akka.core.actors.messages.node.UpdateNodeTopologyMsg;
import org.age.akka.core.actors.messages.worker.NodeBroadcastMessage;
import org.age.akka.core.actors.messages.worker.TaskBroadcastInMsg;
import org.age.akka.core.actors.messages.worker.TaskBroadcastOutMsg;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class NodeActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private ActorRef task;

    private DirectedGraph<NodeId, DefaultEdge> topology;

    private boolean pause;

    private boolean cancel;

    private boolean started;


    public NodeActor() {
        log.info("spawn {} actor", self().path());

        receive(ReceiveBuilder
                .match(StartTaskMsg.class, this::startTask)
                .match(UpdateNodeTopologyMsg.class, this::updateTopology)
                .match(InterruptTaskMsg.class, this::interruptTask)
                .match(IsTaskInterruptedMsg.class, this::getTaskInterruptedStatus)
                .match(TaskBroadcastOutMsg.class, this::broadcast)
                .match(NodeBroadcastMessage.class, this::processBroadcastMessageToTask)
                .matchAny((m -> log.warning("unexpected message {}", m)))
                .build());
    }

    private void processBroadcastMessageToTask(NodeBroadcastMessage msg) {
//        log.info("Received broadcast message {}.", msg);
        task.tell(new TaskBroadcastInMsg(msg.getContent()), self());
    }

    private void broadcast(TaskBroadcastOutMsg msg) {
//        log.info("Sending broadcast message {} ", msg);
        topology.vertexSet().stream().forEach(id -> send(id, msg));
    }

    private void send(NodeId id, TaskBroadcastOutMsg msg) {
//        log.info("Sending to {}", id);
        String path = "akka.tcp://age3@" + id.getHostname() + ":" + id.getPort() + "/user/" + id.getName();
        ActorSelection actorSelection = context().actorSelection(path);
        actorSelection.tell(new NodeBroadcastMessage(msg.getContent()), self());
    }

    @Override
    public void preStart() throws Exception {
        task = context().actorOf(Props.create(SimpleBroadcastCommunicationTaskActor.class), "task");
        context().watch(task);
        log.info("Joined this cluster because I make poor life choices.");
    }

    @Override
    public void postStop() throws Exception {
        context().unwatch(task);
        context().stop(task);
    }

    private void startTask(StartTaskMsg msg) {
        if (started) {
            if (cancel) {
                log.info("task cancelled - cannot resume");
                return;
            }
            if (!pause) {
                log.info("task not paused - cannot resume");
                return;
            }
        }
        log.info("start task message - or resume if was paused");
        log.info("paused = " + pause);
        task.tell(new StartTaskMsg(), self());
        pause = false;
        started = true;
    }

    private void updateTopology(UpdateNodeTopologyMsg msg) {
        topology = msg.getTopology();
        log.info("Node received updated topology: {}", topology);
    }

    private void interruptTask(InterruptTaskMsg msg) {
        log.info("InterruptedTaskMsg received: {}", msg);
        cancel = msg.isCancel();
        pause = msg.isPause();
    }

    private void getTaskInterruptedStatus(IsTaskInterruptedMsg msg) {
        log.info("return current interruption state");
        sender().tell(new TaskInterruptedResponseMsg(pause, cancel), self());
    }

}
