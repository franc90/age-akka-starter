package org.age.akka.core.actors.custom.master;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.custom.master.services.LifecycleServiceActor;
import org.age.akka.core.actors.custom.master.services.TopologyServiceActor;
import org.age.akka.core.actors.custom.master.services.WorkerServiceActor;
import org.age.akka.core.actors.messages.lifecycle.LifecycleMsg;
import org.age.akka.core.actors.messages.task.State;
import org.age.akka.core.actors.messages.task.TaskMsg;
import org.age.akka.core.actors.messages.topology.TopologyUpdatedMsg;
import org.age.akka.core.actors.messages.topology.UpdateTopologyMsg;
import org.age.akka.core.actors.messages.worker.UpdateWorkerTopologiesMsg;
import org.age.akka.core.actors.messages.worker.WorkersTopologiesUpdatedMsg;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MasterActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    private ActorRef lifecycleService;

    private ActorRef topologyService;

    private ActorRef workerService;

    private ActorRef taskService;

    private Queue<LifecycleMsg> lifecycleMessages = new ConcurrentLinkedQueue<>();

    public MasterActor() {
        log.info("init master actor!! " + self().path());

        receive(ReceiveBuilder
                .match(LifecycleMsg.class, this::lifecycleMessage)
                .match(State.class, this::taskStateUpdated)
                .match(TopologyUpdatedMsg.class, this::topologyUpdated)
                .match(WorkersTopologiesUpdatedMsg.class, this::resumeTask)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    public void preStart() throws Exception {
        workerService = context().actorOf(Props.create(WorkerServiceActor.class), "workerService");
        topologyService = context().actorOf(Props.create(TopologyServiceActor.class), "topologyService");
        taskService = context().actorOf(Props.create(TopologyServiceActor.class), "taskService");
        lifecycleService = context().actorOf(Props.create(LifecycleServiceActor.class), "lifecycleService");
        context().watch(lifecycleService);
    }

    private void lifecycleMessage(LifecycleMsg msg) throws Exception {
        log.info("lifecycleMessage ", msg);

        lifecycleMessages.add(msg);
        if (lifecycleMessages.size() > 1) {
            log.info("Lifecycle messages awaiting");
            return;
        }

        updateMembership(msg);
    }

    private void updateMembership(LifecycleMsg msg) throws Exception {
        log.info("updateMembership membership");
        taskService.tell(new TaskMsg(TaskMsg.Type.PAUSE), self());
        workerService.tell(new LifecycleMsg(msg.getType(), msg.getAddress()), self());
    }

    private void taskStateUpdated(State taskState) {
        log.info("task state updated");
        if (taskState == State.PAUSED) {
            topologyService.tell(new UpdateTopologyMsg(), self());
            return;
        }
        log.error("Task not paused");
    }

    private void topologyUpdated(TopologyUpdatedMsg msg) {
        log.info("topology updated");
        workerService.tell(new UpdateWorkerTopologiesMsg(), self());
    }

    private void resumeTask(WorkersTopologiesUpdatedMsg msg) throws Exception {
        log.info("resume task");
        lifecycleMessages.poll();
        if (lifecycleMessages.isEmpty()) {
            log.info("no more tasks - resume");
            taskService.tell(new TaskMsg(TaskMsg.Type.RESUME), self());
            return;
        }

        log.info("tasks yet exist - carry on");
        LifecycleMsg lifecycleMsg = lifecycleMessages.peek();
        updateMembership(lifecycleMsg);
    }

    private void startTasks() {

    }

}
