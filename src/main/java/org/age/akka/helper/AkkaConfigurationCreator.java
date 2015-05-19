package org.age.akka.helper;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.age.akka.exceptions.AkkaClusterNotStartedException;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;

public class AkkaConfigurationCreator {

    private static final String configString = "akka {\n" +
            "  actor {\n" +
            "    provider = \"akka.cluster.ClusterActorRefProvider\"\n" +
            "  }\n" +
            "  remote {\n" +
            "    log-remote-lifecycle-events = off\n" +
            "    netty.tcp {\n" +
            "      hostname = \"%s\"\n" +
            "      port = %s\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  cluster {\n" +
            "    seed-nodes = [\n" +
            "      %s" +
            "    ]\n" +
            "    auto-down-unreachable-after = 10s\n" +
            "  }\n" +
            "}\n";

    public static final String seedNodeString = "      \"akka.tcp://%s@%s:%s\"";

    public static Config createConfiguration(AkkaNodeConfig nodeConfig) {
        checkParams(nodeConfig);

        String hostname = nodeConfig.getCurrentNode().getHostname();
        String port = String.valueOf(nodeConfig.getCurrentNode().getPort());
        String seeds = buildSeedNodesString(nodeConfig.getSeedNodes());

        String formattedConfig = String.format(configString, hostname, port, seeds);

        Config config = ConfigFactory.parseString(formattedConfig);

        return config;
    }

    private static void checkParams(AkkaNodeConfig nodeConfig) {
        if (nodeConfig == null) {
            throw new AkkaClusterNotStartedException("No node config");
        }

        if (nodeConfig.getCurrentNode() == null) {
            throw new AkkaClusterNotStartedException("No current node configuration");
        }

        if (CollectionUtils.isEmpty(nodeConfig.getSeedNodes())) {
            throw new AkkaClusterNotStartedException("No seed nodes configuration");
        }
    }

    private static String buildSeedNodesString(Collection<AkkaNode> seedNodes) {
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
}
