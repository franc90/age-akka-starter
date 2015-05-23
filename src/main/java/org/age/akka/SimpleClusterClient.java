package org.age.akka;

import org.age.akka.helper.AkkaConfigConstants;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;

import java.util.Arrays;

public class SimpleClusterClient {

    public static void main(String[] args) throws InterruptedException {
        AkkaNodeConfig config = AkkaNodeConfig.builder()
                .withCurrentNode(AkkaNode.builder()
                                .withActorSystemName("sys")
                                .withHostname("localhost")
                                .withPort(0)
                                .addRole(AkkaConfigConstants.CLUSTER_WORKER_ROLE)
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
