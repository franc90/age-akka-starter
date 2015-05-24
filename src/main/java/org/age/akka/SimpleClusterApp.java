package org.age.akka;

import org.age.akka.helper.AkkaConfigConstants;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;

import java.util.Arrays;

public class SimpleClusterApp {

    public static void main(String[] args) {
        Integer port = Integer.valueOf(args[0]);

        AkkaNodeConfig config =
                AkkaNodeConfig.builder()
                        .withCurrentNode(AkkaNode.builder()
                                        .withActorSystemName("sys")
                                        .withHostname("localhost")
                                        .withPort(port)
                                        .addRole(AkkaConfigConstants.CLUSTER_MEMBER_ROLE)
                                        .build()
                        ).withSeedNodes(Arrays.asList(
                                AkkaNode.builder()
                                        .withActorSystemName("sys")
                                        .withHostname("localhost")
                                        .withPort(2551)
                                        .build(),
                                AkkaNode.builder()
                                        .withActorSystemName("sys")
                                        .withHostname("localhost")
                                        .withPort(2552)
                                        .build(),
                                AkkaNode.builder()
                                        .withActorSystemName("sys")
                                        .withHostname("localhost")
                                        .withPort(2553)
                                        .build()
                        )
                ).build();

        AkkaStarter akkaStarter = new AkkaStarter();
        akkaStarter.startCluster(config);
    }

}
