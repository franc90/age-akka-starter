package org.age.akka;


import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import org.age.akka.actors.AkkaClusterListener;
import org.age.akka.helper.AkkaConfigurationCreator;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;

public class AkkaStarter {

    public static final String CLUSTER_STATE_ACTOR_NAME = "ClusterStateListener";

    public void startCluster(AkkaNodeConfig nodeConfig) {
        Config configuration = AkkaConfigurationCreator.createConfiguration(nodeConfig);
        AkkaNode currentNode = nodeConfig.getCurrentNode();

        ActorSystem actorSystem = ActorSystem.create(currentNode.getActorSystemName(), configuration);

        Cluster
                .get(actorSystem)
                .registerOnMemberUp(() ->
                                actorSystem.actorOf(Props.create(AkkaClusterListener.class), CLUSTER_STATE_ACTOR_NAME)
                );

        AkkaUtils.setActorSystem(actorSystem);
    }

}
