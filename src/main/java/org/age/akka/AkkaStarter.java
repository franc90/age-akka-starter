package org.age.akka;


import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import org.age.akka.actors.proxy.ClusterProxyActor;
import org.age.akka.actors.proxy.WorkerProxyActor;
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
                                actorSystem.actorOf(Props.create(ClusterProxyActor.class), AkkaConfigConstants.CLUSTER_PROXY_AGENT_NAME)
                );
    }

    public void joinCluster(AkkaNodeConfig nodeConfig) {
        ActorSystem actorSystem = createActorSystem(nodeConfig);
        Cluster
                .get(actorSystem)
                .registerOnMemberUp(() ->
                                actorSystem.actorOf(Props.create(WorkerProxyActor.class), AkkaConfigConstants.WORKER_PROXY_AGENT_NAME)
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
