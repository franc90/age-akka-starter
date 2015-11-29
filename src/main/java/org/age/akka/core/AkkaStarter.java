package org.age.akka.core;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import org.age.akka.core.actors.proxy.ClusterProxyActor;
import org.age.akka.core.actors.proxy.WorkerProxyActor;
import org.age.akka.core.helper.AkkaConfigConstants;
import org.age.akka.core.helper.AkkaConfigurationCreator;
import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.data.ClusterConfigHolder;

public class AkkaStarter {

    private AkkaConfigurationCreator configurationCreator;

    public AkkaStarter() {
        configurationCreator = new AkkaConfigurationCreator();
    }

    public void startCluster(ClusterConfigHolder nodeConfig) {
        ActorSystem actorSystem = createActorSystem(nodeConfig);
        Cluster
                .get(actorSystem)
                .registerOnMemberUp(() ->
                        actorSystem.actorOf(Props.create(ClusterProxyActor.class), AkkaConfigConstants.CLUSTER_PROXY_AGENT_NAME)
                );
    }

    public void joinCluster(ClusterConfigHolder nodeConfig) {
        ActorSystem actorSystem = createActorSystem(nodeConfig);
        Cluster
                .get(actorSystem)
                .registerOnMemberUp(() ->
                        actorSystem.actorOf(Props.create(WorkerProxyActor.class), AkkaConfigConstants.WORKER_PROXY_AGENT_NAME)
                );
    }

    private ActorSystem createActorSystem(ClusterConfigHolder nodeConfig) {
        AkkaUtils.setConfig(nodeConfig);

        Config configuration = configurationCreator.createConfiguration(nodeConfig);
        AkkaNode currentNode = nodeConfig.getCurrentNode();

        ActorSystem actorSystem = ActorSystem.create(currentNode.getActorSystemName().getName(), configuration);
        AkkaUtils.setActorSystem(actorSystem);

        return actorSystem;
    }
}
