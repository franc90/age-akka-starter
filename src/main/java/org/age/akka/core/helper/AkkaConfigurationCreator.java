package org.age.akka.core.helper;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.age.akka.start.common.data.ActorSystemName;
import org.age.akka.start.common.data.AkkaNode;
import org.age.akka.start.common.data.ClusterConfigHolder;
import org.age.akka.start.common.data.Hostname;
import org.age.akka.start.common.data.Port;
import org.age.akka.start.common.data.Role;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;

@Named
public class AkkaConfigurationCreator {

    private static final Logger log = LoggerFactory.getLogger(AkkaConfigurationCreator.class);

    private static final String seedNodeString = "\"akka.tcp://%s@%s:%s\"";

    private static final String roleString = "\"%s\"";

    @Inject
    private AkkaConfigurationLoader configurationLoader;

    public Config createConfiguration(ClusterConfigHolder nodeConfig) {
        log.info("Creating configuration from config holder: " + nodeConfig);
        Preconditions.checkNotNull(nodeConfig, "No node config");
        Preconditions.checkNotNull(nodeConfig.getCurrentNode(), "No current node configuration");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(nodeConfig.getClusterNodes()), "No seed nodes configuration");

        String configTemplate = configurationLoader.loadConfigurationTemplate();
        log.trace("Loaded config template\n" + configTemplate);

        Hostname hostname = nodeConfig.getCurrentNode().getHostname();
        Port port = nodeConfig.getCurrentNode().getPort();
        String seeds = buildSeedNodesString(nodeConfig.getClusterNodes());
        String roles = buildRoles(nodeConfig.getCurrentNode().getRoles());

        String config = String.format(configTemplate, hostname.getHostname(), port.stringValue(), seeds, roles);

        log.info("Created Akka config\n" + config);
        return ConfigFactory.parseString(config);
    }

    private String buildSeedNodesString(Collection<AkkaNode> seedNodes) {
        String returnVal = seedNodes
                .stream()
                .map(node -> {
                    ActorSystemName actorSystemName = node.getActorSystemName();
                    Hostname hostname = node.getHostname();
                    Port port = node.getPort();
                    return String.format(seedNodeString, actorSystemName.getName(), hostname.getHostname(), port.stringValue());
                })
                .reduce((u, v) -> u + ",\n" + v)
                .orElse("NO_SEED_NODES");

        return returnVal;
    }

    private String buildRoles(List<Role> roles) {
        return roles
                .stream()
                .map(role -> String.format(roleString, role.getRoleName()))
                .reduce((a, b) -> a + " , " + b)
                .orElse("");
    }
}
