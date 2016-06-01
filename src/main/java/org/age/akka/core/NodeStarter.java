package org.age.akka.core;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import org.age.akka.core.actors.custom.master.MasterActor;
import org.age.akka.core.actors.custom.worker.WorkerActor;
import org.age.akka.core.helper.AkkaConfigurationCreator;
import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.data.Hostname;
import org.age.akka.start.common.data.Port;
import org.age.akka.start.common.utils.ClusterDataHolder;
import org.jboss.netty.channel.ChannelException;
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

    public ActorSystem startCluster(ClusterConfigHolder nodeConfig) {
        actorSystem = createActorSystem(nodeConfig);

        log.info("Actor system " + actorSystem);

        Cluster
                .get(actorSystem)
                .registerOnMemberUp(() -> {
                    String nodeName = createNodeName(nodeConfig);
                    actorSystem.actorOf(Props.create(MasterActor.class), "master");
                    actorSystem.actorOf(Props.create(WorkerActor.class), nodeName);
                });

        return actorSystem;
    }

    public ActorSystem createWorker(ClusterConfigHolder nodeConfig) {
        if (actorSystem != null) {
            log.trace("Actor system already exists");
            return actorSystem;
        }

        for (int i = 0; i < 10 && actorSystem == null; ++i) {
            try {
                actorSystem = createActorSystem(nodeConfig);
            } catch (ChannelException ex) {
                log.warn("Could not open connection yet");
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Cluster
                .get(actorSystem)
                .registerOnMemberUp(() -> {
                    String nodeName = createNodeName(nodeConfig);
                    actorSystem.actorOf(Props.create(WorkerActor.class), nodeName);
                });

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
        clusterDataHolder.setActorSystem(actorSystem);

        return actorSystem;
    }

    private String createNodeName(ClusterConfigHolder nodeConfig) {
        Hostname hostname = nodeConfig.getCurrentNode().getHostname();
        Port port = nodeConfig.getCurrentNode().getPort();

        return "node_" + hostname.getHostname() + "_" + port.getPort()
                ;
    }
}
