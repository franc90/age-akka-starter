package org.age.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;

import java.util.Arrays;

public class SimpleClusterApp {

    public static void main(String[] args) {
        AkkaNodeConfig config = AkkaNodeConfig.builder()
                .withCurrentNode(AkkaNode.builder()
                                .withActorSystemName("sys")
                                .withHostname("localhost")
                                .withPort(2551)
                                .build()
                ).withSeedNodes(Arrays.asList(AkkaNode.builder()
                        .withActorSystemName("sys")
                        .withHostname("localhost")
                        .withPort(2551)
                        .build()))
                .build();

        AkkaStarter akkaStarter = new AkkaStarter();
        akkaStarter.startCluster(config);

        ActorRef inner = AkkaUtils.getActorSystem().actorOf(Props.create(InnerActor.class), "inner");

        System.out.println("Actor created: " + inner.path().toSerializationFormat());

        ActorRef inner2 = AkkaUtils.getActorSystem().actorFor("user/inner");
        System.out.println("System terminated: " + AkkaUtils.getActorSystem().isTerminated());
        System.out.println("IS terminated: " + inner.isTerminated());
        System.out.println(inner.path().toSerializationFormat());

        inner2.tell(new Command(Command.Type.NEW, "ac1"), ActorRef.noSender());
        inner2.tell(new Command(Command.Type.LIST, "ac1"), ActorRef.noSender());
        inner2.tell(new Command(Command.Type.NEW, "someAct"), ActorRef.noSender());
        inner2.tell(new Command(Command.Type.LIST, "ac1"), ActorRef.noSender());
    }

}
