package org.age.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;

import java.util.Arrays;

public class SimpleClusterClient {

    public static void main(String[] args) {
        AkkaNodeConfig config = AkkaNodeConfig.builder()
                .withCurrentNode(AkkaNode.builder()
                                .withActorSystemName("sys")
                                .withHostname("localhost")
                                .withPort(2552)
                                .build()
                ).withSeedNodes(Arrays.asList(AkkaNode.builder()
                        .withActorSystemName("sys")
                        .withHostname("localhost")
                        .withPort(2551)
                        .build()))
                .build();

        AkkaStarter akkaStarter = new AkkaStarter();
        akkaStarter.startCluster(config);

        AkkaUtils.getActorSystem().actorFor("inner").tell(new Command(Command.Type.NEW, "ac1"), ActorRef.noSender());
        AkkaUtils.getActorSystem().actorFor("inner").tell(new Command(Command.Type.LIST, "ac1"), ActorRef.noSender());
        AkkaUtils.getActorSystem().actorFor("inner").tell(new Command(Command.Type.NEW, "someAct"), ActorRef.noSender());
        AkkaUtils.getActorSystem().actorFor("inner").tell(new Command(Command.Type.LIST, "ac1"), ActorRef.noSender());
    }

}
