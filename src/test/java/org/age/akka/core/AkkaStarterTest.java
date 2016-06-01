package org.age.akka.core;

import akka.actor.ActorSystem;
import org.age.akka.start.common.data.*;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class AkkaStarterTest {

    @Test
    public void testStartingComputationCluster() throws Exception {
        NodeStarter nodeStarter = new NodeStarter();

        nodeStarter.startCluster(generateClusterNodeConfig());

        ActorSystem actorSystem = AkkaUtils.getActorSystem();
        assertThat(actorSystem).isNotNull();
    }

    private ClusterConfigHolder generateClusterNodeConfig() {
        return ClusterConfigHolder.builder()
                .withCurrentNode(AkkaNode.builder()
                        .withActorSystemName(new ActorSystemName("testSystem"))
                        .withHostname(new Hostname("localhost"))
                        .withPort(new Port(7896))
                        .addRole(new Role("master"))
                        .build())
                .withClusterNodes(Arrays.asList(AkkaNode.builder()
                        .withActorSystemName(new ActorSystemName("testSystem"))
                        .withHostname(new Hostname("localhost"))
                        .withPort(new Port(7896))
                        .addRole(new Role("master"))
                        .build()))
                .build();
    }

}