package org.age.akka.core.helper;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.age.akka.core.exceptions.AkkaClusterNotStartedException;
import org.age.akka.start.data.AkkaNode;
import org.age.akka.start.data.ClusterConfigHolder;
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

        String hostname = nodeConfig.getCurrentNode().getHostname();
        String port = String.valueOf(nodeConfig.getCurrentNode().getPort());
        String seeds = buildSeedNodesString(nodeConfig.getClusterNodes());
        String roles = buildRoles(nodeConfig.getCurrentNode().getRoles());

        String formattedConfig = String.format(configString, hostname, port, seeds, roles);

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
                    String actorSystemName = node.getActorSystemName();
                    String hostname = node.getHostname();
                    String port = String.valueOf(node.getPort());
                    return String.format(seedNodeString, actorSystemName, hostname, port);
                })
                .reduce((u, v) -> u + ",\n" + v)
                .get();

        return returnVal + "\n";
    }

    private String buildRoles(List<String> roles) {
        return roles
                .stream()
                .map(role -> String.format(roleString, role))
                .reduce((a, b) -> a + " , " + b)
                .orElse("");
    }
}
