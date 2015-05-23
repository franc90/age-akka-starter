package org.age.akka;

import akka.actor.ActorRef;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SimpleClusterClient {

    public static void main(String[] args) throws InterruptedException {
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

        TimeUnit.SECONDS.sleep(4);

        ActorRef inner = AkkaUtils.getActorSystem().actorFor("user/inner");
        System.out.println("System terminated: " + AkkaUtils.getActorSystem().isTerminated());
        System.out.println("IS terminated: " + inner.isTerminated());
        System.out.println(inner.path().toSerializationFormat());

        inner.tell(new Command(Command.Type.NEW, "ac1"), ActorRef.noSender());
        inner.tell(new Command(Command.Type.LIST, "ac1"), ActorRef.noSender());
        inner.tell(new Command(Command.Type.NEW, "someAct"), ActorRef.noSender());
        inner.tell(new Command(Command.Type.LIST, "ac1"), ActorRef.noSender());
    }

}
