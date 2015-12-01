package org.age.akka.core;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import org.age.akka.core.actors.proxy.ClusterProxyActor;
import org.age.akka.core.actors.proxy.WorkerProxyActor;
import org.age.akka.core.data.StartedCluster;
import org.age.akka.core.helper.AkkaConfigConstants;
import org.age.akka.core.helper.AkkaConfigurationCreator;
import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.utils.ClusterDataHolder;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class NodeStarter {

    @Inject
    private AkkaConfigurationCreator configurationCreator;

    @Inject
    private ClusterDataHolder clusterDataHolder;

    private ActorSystem actorSystem;

    public StartedCluster startCluster(ClusterConfigHolder nodeConfig) {
        actorSystem = createActorSystem(nodeConfig);
        Cluster.get(actorSystem)
                .registerOnMemberUp(() ->
                        actorSystem.actorOf(Props.create(ClusterProxyActor.class), AkkaConfigConstants.CLUSTER_PROXY_AGENT_NAME)
                );

        clusterDataHolder.setActorSystem(actorSystem);
        clusterDataHolder.setWorkerCreated(true);

        return new StartedCluster(actorSystem, true);
    }

    public StartedCluster joinCluster(ClusterConfigHolder nodeConfig) {
        actorSystem = createActorSystem(nodeConfig);
        clusterDataHolder.setActorSystem(actorSystem);
        clusterDataHolder.setWorkerCreated(actorSystem != null);

        return new StartedCluster(actorSystem, true);
    }

    public ActorSystem createWorker(ClusterConfigHolder nodeConfig) {
        if (actorSystem != null) {
            return actorSystem;
        }

        actorSystem = createActorSystem(nodeConfig);
        Cluster.get(actorSystem)
                .registerOnMemberUp(() ->
                        actorSystem.actorOf(Props.create(WorkerProxyActor.class), AkkaConfigConstants.WORKER_PROXY_AGENT_NAME)
                );

        clusterDataHolder.setActorSystem(actorSystem);
        clusterDataHolder.setWorkerCreated(true);
        return actorSystem;
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
