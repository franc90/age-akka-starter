package org.age.akka;


import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import org.age.akka.actors.ClusterManagingActor;
import org.age.akka.actors.WorkerManagingActor;
import org.age.akka.helper.AkkaConfigConstants;
import org.age.akka.helper.AkkaConfigurationCreator;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;

public class AkkaStarter {

    public void startCluster(AkkaNodeConfig nodeConfig) {
        ActorSystem actorSystem = createActorSystem(nodeConfig);
        Cluster
                .get(actorSystem)
                .registerOnMemberUp(() ->
                                actorSystem.actorOf(Props.create(ClusterManagingActor.class), AkkaConfigConstants.CLUSTER_MANAGING_AGENT_NAME)
                );
    }

    public void joinCluster(AkkaNodeConfig nodeConfig) {
        ActorSystem actorSystem = createActorSystem(nodeConfig);
        Cluster
                .get(actorSystem)
                .registerOnMemberUp(() ->
                                actorSystem.actorOf(Props.create(WorkerManagingActor.class), AkkaConfigConstants.WORKER_MANAGING_AGENT_NAME)
                );
    }

    private ActorSystem createActorSystem(AkkaNodeConfig nodeConfig) {
        AkkaUtils.setConfig(nodeConfig);

        Config configuration = AkkaConfigurationCreator.createConfiguration(nodeConfig);
        AkkaNode currentNode = nodeConfig.getCurrentNode();

        ActorSystem actorSystem = ActorSystem.create(currentNode.getActorSystemName(), configuration);
        AkkaUtils.setActorSystem(actorSystem);

        return actorSystem;
    }
}
