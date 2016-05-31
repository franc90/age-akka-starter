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
import org.age.akka.core.actors.messages.node.InterruptTaskMsg;
import org.age.akka.core.actors.messages.node.StartTaskMsg;
import org.age.akka.core.actors.messages.node.UpdateNodeTopologyMsg;
import org.age.akka.core.actors.messages.task.TaskStateMsg;
import org.age.akka.core.actors.messages.worker.ActorAddedMsg;
import org.age.akka.core.actors.messages.worker.AddMemberMsg;
import org.age.akka.core.actors.messages.worker.AddingActorFailedMsg;
import org.age.akka.core.actors.messages.worker.GetNodesMsg;
import org.age.akka.core.actors.messages.worker.NodesMsg;
import org.age.akka.core.actors.messages.worker.RemoveMemberMsg;
import org.age.akka.core.actors.messages.worker.UpdateWorkerTopologiesMsg;
import org.age.akka.core.actors.messages.worker.WorkersTopologiesUpdatedMsg;
import org.age.akka.start.common.utils.SleepUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkerServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private static final int MAX_CONNECTION_ATTEMPTS = 20;

    private final Map<NodeId, ActorRef> memberNodes = new HashMap<>();

    private final Map<String, Tuple2<Integer, NodeId>> actorConnectionCount = new HashMap<>();

    public WorkerServiceActor() {
        receive(ReceiveBuilder
                .match(AddMemberMsg.class, this::addMember)
                .match(ActorIdentity.class, this::actorIdentity)
                .match(RemoveMemberMsg.class, this::removeMember)
                .match(TaskStateMsg.class, this::changeTaskState)
                .match(GetNodesMsg.class, this::getNodes)
                .match(UpdateWorkerTopologiesMsg.class, this::updateTopologies)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    private void addMember(AddMemberMsg msg) throws Exception {
        log.info("add Member");
        Address address = msg.getActorAddress();
        NodeId id = NodeId.fromAddress(address);
        log.info("Look for member node at {}", id);

        String path = "akka.tcp://age3@" + id.getHostname() + ":" + id.getPort() + "/user/" + id.getName();
        actorConnectionCount.putIfAbsent(path, Tuple.of(0, id));

        ActorSelection actorSelection = getContext().actorSelection(path);
        actorSelection.tell(new Identify(path), self());
    }

    private void actorIdentity(ActorIdentity identity) {
        ActorRef ref = identity.getRef();
        String path = (String) identity.correlationId();

        Tuple2<Integer, NodeId> tuple = actorConnectionCount.get(path);
        Integer count = tuple._1;
        if (ref == null) {
            if (count < MAX_CONNECTION_ATTEMPTS) {
                log.info("Connection try #{} to {} unsuccessful.", count, path);
                actorConnectionCount.put(path, Tuple.of(count + 1, tuple._2));
                SleepUtils.sleep(200L);
                ActorSelection actorSelection = getContext().actorSelection(path);
                actorSelection.tell(new Identify(path), self());
            } else {
                actorConnectionCount.remove(path);
                log.warning("Cannot find actor at path {}.", path);
                context().parent().tell(new AddingActorFailedMsg(tuple._2), self());
            }
        } else {
            actorConnectionCount.remove(path);
            NodeId id = tuple._2;
            memberNodes.put(id, ref);
            context().parent().tell(new ActorAddedMsg(id), self());
        }
    }

    private void removeMember(RemoveMemberMsg msg) {
        log.info("remove Member");
        NodeId id = NodeId.fromAddress(msg.getAddress());

        log.info("Remove member node from {}", id);
        memberNodes.remove(id);
    }

    private void getNodes(GetNodesMsg msg) {
        log.info("get current cluster nodes");
        Set<NodeId> nodeIds = new HashSet<>(memberNodes.keySet());
        sender().tell(new NodesMsg(nodeIds), self());
    }

    private void changeTaskState(TaskStateMsg msg) {
        log.info("sending {} to {} member nodes", msg.getType(), memberNodes.size());
        switch (msg.getType()) {
            case START:
            case RESUME:
                memberNodes.values().forEach(node -> node.tell(new StartTaskMsg(), self()));
                break;
            case PAUSE:
                memberNodes.values().forEach(node -> node.tell(new InterruptTaskMsg(true, false), self()));
                break;
            case CANCEL:
                memberNodes.values().forEach(context()::stop);
                break;
        }
    }


    private void updateTopologies(UpdateWorkerTopologiesMsg msg) {
        log.info("update worker topologies");
        memberNodes.values().forEach(node -> node.tell(new UpdateNodeTopologyMsg(msg.getTopology()), self()));
        context().parent().tell(new WorkersTopologiesUpdatedMsg(), self());
    }
}
