package org.age.akka;

import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.Member;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;

import java.util.Arrays;
import java.util.Iterator;
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
        akkaStarter.joinCluster(config);
    }

}
