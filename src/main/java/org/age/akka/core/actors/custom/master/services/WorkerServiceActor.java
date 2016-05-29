package org.age.akka.core.actors.custom.master.services;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.Deploy;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.remote.RemoteScope;
import org.age.akka.core.actors.custom.NodeActor;
import org.age.akka.core.actors.custom.NodeId;
import org.age.akka.core.actors.messages.node.UpdateNodeTopologyMsg;
import org.age.akka.core.actors.messages.task.TaskStateMsg;
import org.age.akka.core.actors.messages.worker.AddMemberMsg;
import org.age.akka.core.actors.messages.worker.GetNodesMsg;
import org.age.akka.core.actors.messages.worker.NodesMsg;
import org.age.akka.core.actors.messages.worker.RemoveMemberMsg;
import org.age.akka.core.actors.messages.worker.UpdateWorkerTopologiesMsg;
import org.age.akka.core.actors.messages.worker.WorkersTopologiesUpdatedMsg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorkerServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    private final Map<NodeId, ActorRef> memberNodes = new HashMap<>();

    public WorkerServiceActor() {
        receive(ReceiveBuilder
                .match(AddMemberMsg.class, this::addMember)
                .match(RemoveMemberMsg.class, this::removeMember)
                .match(TaskStateMsg.class, this::changeTaskState)
                .match(GetNodesMsg.class, this::getNodes)
                .match(UpdateWorkerTopologiesMsg.class, this::updateTopologies)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    private void getNodes(GetNodesMsg msg) {
        log.info("get current cluster nodes");
        Set<NodeId> nodeIds = new HashSet<>(memberNodes.keySet());
        sender().tell(new NodesMsg(nodeIds), self());
    }

    private void changeTaskState(TaskStateMsg msg) {
        log.info("sending to " + memberNodes.size() + " member nodes " + msg.getType());
        if (msg.getType() == TaskStateMsg.Type.RESUME) {
//            memberNodes.values().forEach(node -> node.tell(ResumeWorkMessage.class, self()));
        } else if (msg.getType() == TaskStateMsg.Type.PAUSE) {
//            memberNodes.values().forEach(node -> node.tell(PauseWorkMessage.class, self()));
        }
    }

    private void addMember(AddMemberMsg msg) {
        log.info("add Member");
        Address address = msg.getActorAddress();
        NodeId id = NodeId.fromAddress(address);
        log.info("Create member node at " + id);

        Deploy remoteDeploy = new Deploy(new RemoteScope(address));
        ActorRef node = context().system().actorOf(Props.create(NodeActor.class).withDeploy(remoteDeploy));
        memberNodes.putIfAbsent(id, node);
    }

    private void removeMember(RemoveMemberMsg msg) {
        log.info("remove Member");
        NodeId id = NodeId.fromAddress(msg.getAddress());

        log.info("Remove member node from " + id);
        memberNodes.remove(id);
    }

    private void updateTopologies(UpdateWorkerTopologiesMsg msg) {
        log.info("update worker topologies");
        memberNodes.values().forEach(node -> node.tell(new UpdateNodeTopologyMsg(msg.getTopology()), self()));
        context().parent().tell(new WorkersTopologiesUpdatedMsg(), self());
    }
}
