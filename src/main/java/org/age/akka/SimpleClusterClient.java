package org.age.akka;

import org.age.akka.core.AkkaStarter;
import org.age.akka.core.helper.AkkaConfigConstants;
import org.age.akka.start.data.AkkaNode;
import org.age.akka.start.data.ClusterConfigHolder;

import java.util.Arrays;

@Deprecated
public class SimpleClusterClient {

    public static void main(String[] args) throws InterruptedException {
        ClusterConfigHolder config =
                ClusterConfigHolder.builder()
                        .withCurrentNode(AkkaNode.builder()
                                        .withActorSystemName("sys")
                                        .withHostname("localhost")
                                        .withPort(0)
                                        .addRole(AkkaConfigConstants.CLUSTER_WORKER_ROLE)
                                        .build()
                        ).withClusterNodes(Arrays.asList(
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
        akkaStarter.joinCluster(config);
    }

}
