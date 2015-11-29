package org.age.akka.core.helper;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.age.akka.core.exceptions.AkkaClusterNotStartedException;
import org.age.akka.start.common.data.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class AkkaConfigurationCreator {

    private static final String seedNodeString = "      \"akka.tcp://%s@%s:%s\"";

    private static final String roleString = "\"%s\"";

    private AkkaConfigurationLoader configurationLoader;

    public AkkaConfigurationCreator() {
        this.configurationLoader = new AkkaConfigurationLoader();
    }

    public Config createConfiguration(ClusterConfigHolder nodeConfig) {
        checkParams(nodeConfig);

        String configString = configurationLoader.loadConfigurationTemplate();

        Hostname hostname = nodeConfig.getCurrentNode().getHostname();
        Port port = nodeConfig.getCurrentNode().getPort();
        String seeds = buildSeedNodesString(nodeConfig.getClusterNodes());
        String roles = buildRoles(nodeConfig.getCurrentNode().getRoles());

        String formattedConfig = String.format(configString, hostname.getHostname(), port.stringValue(), seeds, roles);

        return ConfigFactory.parseString(formattedConfig);
    }

    private void checkParams(ClusterConfigHolder nodeConfig) {
        if (nodeConfig == null) {
            throw new AkkaClusterNotStartedException("No node config");
        }

        if (nodeConfig.getCurrentNode() == null) {
            throw new AkkaClusterNotStartedException("No current node configuration");
        }

        if (CollectionUtils.isEmpty(nodeConfig.getClusterNodes())) {
            throw new AkkaClusterNotStartedException("No seed nodes configuration");
        }
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
                .get();

        return returnVal + "\n";
    }

    private String buildRoles(List<Role> roles) {
        return roles
                .stream()
                .map(role -> String.format(roleString, role.getRoleName()))
                .reduce((a, b) -> a + " , " + b)
                .orElse("");
    }
}
