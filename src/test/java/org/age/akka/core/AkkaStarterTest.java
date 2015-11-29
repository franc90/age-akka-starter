package org.age.akka.core;

import akka.actor.ActorSystem;
import org.age.akka.start.common.data.*;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class AkkaStarterTest {

    @Test
    public void testStartingComputationCluster() throws Exception {
        AkkaStarter akkaStarter = new AkkaStarter();

        akkaStarter.startCluster(generateClusterNodeConfig());

        ActorSystem actorSystem = AkkaUtils.getActorSystem();
        assertThat(actorSystem).isNotNull();

//        it won't work this way. shade.
//        Future<ActorRef> future = actorSystem.actorSelection("user/" + AkkaConfigConstants.CLUSTER_PROXY_AGENT_NAME).resolveOne(new Timeout(7000));
//        assertThat(future.isCompleted()).isTrue();
//        ActorRef actorRef = Await.result(future, new FiniteDuration(1, TimeUnit.SECONDS));
//        assertThat(actorRef).isNotNull();


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