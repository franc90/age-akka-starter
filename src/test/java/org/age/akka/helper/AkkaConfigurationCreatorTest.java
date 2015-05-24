package org.age.akka.helper;

import com.typesafe.config.Config;
import org.age.akka.exceptions.AkkaClusterNotStartedException;
import org.age.akka.structures.AkkaNode;
import org.age.akka.structures.AkkaNodeConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AkkaConfigurationCreatorTest {

    public static final String ACTOR_SYSTEM_NAME = "ACTOR_SYSTEM_NAME";
    public static final String CURRENT_HOST = "132.11.34.123";
    public static final int CURRENT_PORT = 2551;

    public static final String NODE_HOSTNAME = "162.23.58.1";
    public static final int NODES_CNT = 3;

    private List<AkkaNode> nodes;

    private AkkaNode currentNode;

    @Before
    public void init() {
        currentNode = AkkaNode.builder()
                .withActorSystemName(ACTOR_SYSTEM_NAME)
                .withHostname(CURRENT_HOST)
                .withPort(CURRENT_PORT)
                .build();

        nodes = new ArrayList<>();
        for (int i = 0; i < NODES_CNT; ++i) {
            nodes.add(AkkaNode.builder()
                            .withActorSystemName(ACTOR_SYSTEM_NAME)
                            .withHostname(NODE_HOSTNAME + i)
                            .withPort(i + 1)
                            .build()
            );
        }
    }

    @Test(expected = AkkaClusterNotStartedException.class)
    public void noCurrentNodeTest() {
        AkkaConfigurationCreator.createConfiguration(AkkaNodeConfig
                .builder()
                .withCurrentNode(null)
                .withSeedNodes(nodes)
                .build());
    }

    @Test(expected = AkkaClusterNotStartedException.class)
    public void noSeedNodesTest() {
        AkkaConfigurationCreator.createConfiguration(AkkaNodeConfig
                .builder()
                .withCurrentNode(currentNode)
                .withSeedNodes(new ArrayList<>())
                .build());
    }

    @Test
    public void configurationCreationTest() {
        AkkaNodeConfig nodeConfig = AkkaNodeConfig
                .builder()
                .withCurrentNode(currentNode)
                .withSeedNodes(nodes)
                .build();

        Config config = AkkaConfigurationCreator.createConfiguration(nodeConfig);
        int currentPort = config.getInt("akka.remote.netty.tcp.port");
        assertEquals(CURRENT_PORT, currentPort);

        String currentHostname = config.getString("akka.remote.netty.tcp.hostname");
        assertEquals(CURRENT_HOST, currentHostname);

        List<String> seedNodes = config.getStringList("akka.cluster.seed-nodes");
        assertNotNull(seedNodes);
        assertEquals(NODES_CNT, seedNodes.size());

        for (int i = 0; i < seedNodes.size(); i++) {
            String expected = buildSeedNodes(i);
            assertEquals(expected, seedNodes.get(i));
        }

    }

    private String buildSeedNodes(int i) {
        return "akka.tcp://" + ACTOR_SYSTEM_NAME + "@" + NODE_HOSTNAME + i + ":" + (i + 1);
    }

}