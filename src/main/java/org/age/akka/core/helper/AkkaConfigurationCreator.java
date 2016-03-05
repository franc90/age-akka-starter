package org.age.akka.core.helper;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.age.akka.start.common.data.*;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;

@Named
public class AkkaConfigurationCreator {

    private static final String seedNodeString = "      \"akka.tcp://%s@%s:%s\"";

    private static final String roleString = "\"%s\"";

    @Inject
    private AkkaConfigurationLoader configurationLoader;

    public Config createConfiguration(ClusterConfigHolder nodeConfig) {
        Preconditions.checkNotNull(nodeConfig, "No node config");
        Preconditions.checkNotNull(nodeConfig.getCurrentNode(), "No current node configuration");
        Preconditions.checkArgument(CollectionUtils.isEmpty(nodeConfig.getClusterNodes()), "No seed nodes configuration");

        String configString = configurationLoader.loadConfigurationTemplate();

        Hostname hostname = nodeConfig.getCurrentNode().getHostname();
        Port port = nodeConfig.getCurrentNode().getPort();
        String seeds = buildSeedNodesString(nodeConfig.getClusterNodes());
        String roles = buildRoles(nodeConfig.getCurrentNode().getRoles());

        String formattedConfig = String.format(configString, hostname.getHostname(), port.stringValue(), seeds, roles);

        return ConfigFactory.parseString(formattedConfig);
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
