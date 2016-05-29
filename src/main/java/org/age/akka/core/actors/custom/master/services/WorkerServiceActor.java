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
import org.age.akka.core.actors.messages.AddMemberMsg;
import org.age.akka.core.actors.messages.HelloMessage;
import org.age.akka.core.actors.messages.Message;
import org.age.akka.core.actors.messages.RemoveMemberMsg;
import org.age.akka.core.actors.messages.SendMsgMessage;

import java.util.HashMap;
import java.util.Map;

public class WorkerServiceActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());

    private final Map<NodeId, ActorRef> memberNodes = new HashMap<>();

    public WorkerServiceActor() {
        receive(ReceiveBuilder
                .match(AddMemberMsg.class, this::addMember)
                .match(RemoveMemberMsg.class, this::removeMember)
                .match(SendMsgMessage.class, this::sendToAllWorkers)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
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

    private void sendToAllWorkers(SendMsgMessage msg) {
        log.info("send to all workers " + msg);
        System.out.println(msg);
        System.out.println(msg.getContent());

        memberNodes.values().forEach(worker -> {

            HelloMessage content = (HelloMessage) msg.getContent();
            worker.tell(content, sender());
        });
    }
}
