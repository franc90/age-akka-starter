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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class NodeStarter {

    private static final Logger log = LoggerFactory.getLogger(NodeStarter.class);

    @Inject
    private AkkaConfigurationCreator configurationCreator;

    @Inject
    private ClusterDataHolder clusterDataHolder;

    private ActorSystem actorSystem;

    public StartedCluster startCluster(ClusterConfigHolder nodeConfig) {
        actorSystem = createActorSystem(nodeConfig);

        log.info("Actor system " + actorSystem);

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
        log.trace("Creating actor system");

        Config configuration = configurationCreator.createConfiguration(nodeConfig);
        log.trace("created configuration: " + configuration);

        AkkaNode currentNode = nodeConfig.getCurrentNode();
        log.trace("Current node " + currentNode);

        ActorSystem actorSystem = ActorSystem.create(currentNode.getActorSystemName().getName(), configuration);
        AkkaUtils.setActorSystem(actorSystem);

        log.trace("Returning created actor system " + actorSystem);
        return actorSystem;
    }
}
