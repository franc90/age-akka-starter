package org.age.akka.core.actors.custom.master;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.custom.master.services.LifecycleServiceActor;
import org.age.akka.core.actors.custom.master.services.TaskServiceActor;
import org.age.akka.core.actors.custom.master.services.TopologyServiceActor;
import org.age.akka.core.actors.custom.master.services.WorkerServiceActor;
import org.age.akka.core.actors.messages.lifecycle.LifecycleMsg;
import org.age.akka.core.actors.messages.task.StateMsg;
import org.age.akka.core.actors.messages.task.TaskStateMsg;
import org.age.akka.core.actors.messages.topology.TopologyUpdatedMsg;
import org.age.akka.core.actors.messages.topology.UpdateTopologyMsg;
import org.age.akka.core.actors.messages.worker.AddMemberMsg;
import org.age.akka.core.actors.messages.worker.MemberStateUpdateMsg;
import org.age.akka.core.actors.messages.worker.RemoveMemberMsg;
import org.age.akka.core.actors.messages.worker.UpdateWorkerTopologiesMsg;
import org.age.akka.core.actors.messages.worker.WorkersTopologiesUpdatedMsg;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class
MasterActor extends AbstractActor {

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
                .match(LifecycleMsg.class, this::processLifecycleMessage)
                .match(StateMsg.class, this::taskStateUpdated)
                .match(TopologyUpdatedMsg.class, this::topologyUpdated)
                .match(WorkersTopologiesUpdatedMsg.class, this::resumeTask)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    public void preStart() throws Exception {
        workerService = context().actorOf(Props.create(WorkerServiceActor.class), "workerService");
        topologyService = context().actorOf(Props.create(TopologyServiceActor.class), "topologyService");
        taskService = context().actorOf(Props.create(TaskServiceActor.class), "taskService");
        lifecycleService = context().actorOf(Props.create(LifecycleServiceActor.class), "lifecycleService");
        context().watch(workerService);
        context().watch(topologyService);
        context().watch(taskService);
        context().watch(lifecycleService);
    }

    private void processLifecycleMessage(LifecycleMsg msg) throws Exception {
        log.info("new lifecycle message: " + msg);

        lifecycleMessages.add(msg);
        if (lifecycleMessages.size() > 1) {
            log.info("already processing lifecycle message. Adding this one to queue");
            return;
        }

        updateNodesMembership(msg);
    }

    private void updateNodesMembership(LifecycleMsg msg) throws Exception {
        log.info("update nodes membership based on " + msg);
        taskService.tell(new TaskStateMsg(TaskStateMsg.Type.PAUSE), self());
        MemberStateUpdateMsg workerMsg = prepareMessage(msg);
        workerService.tell(workerMsg, self());
    }

    private MemberStateUpdateMsg prepareMessage(LifecycleMsg msg) {
        if (msg.getType() == LifecycleMsg.Type.REMOVE) {
            return new RemoveMemberMsg(msg.getAddress());
        }
        return new AddMemberMsg(msg.getAddress());
    }

    private void taskStateUpdated(StateMsg taskStateMsg) {
        log.info("task state updated");
        if (taskStateMsg == StateMsg.PAUSED) {
            topologyService.tell(new UpdateTopologyMsg(), self());
            return;
        }
        log.error("Task not paused");
    }

    private void topologyUpdated(TopologyUpdatedMsg msg) {
        log.info("topology updated");
        workerService.tell(new UpdateWorkerTopologiesMsg(msg.getTopology()), self());
    }

    private void resumeTask(WorkersTopologiesUpdatedMsg msg) throws Exception {
        lifecycleMessages.poll();
        if (lifecycleMessages.isEmpty()) {
            log.info("no more lifecycle messages - resume task if paused");
            taskService.tell(new TaskStateMsg(TaskStateMsg.Type.RESUME), self());
            return;
        }

        log.info("more tasks queued, update nodes membership");
        LifecycleMsg lifecycleMsg = lifecycleMessages.peek();
        updateNodesMembership(lifecycleMsg);
    }

    private void startTasks() {

    }

}
