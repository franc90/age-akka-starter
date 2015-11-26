package org.age.akka.core.helper;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class AkkaConfigurationCreatorTest {

    private static final String EXPECTED_CONFIG = "akka {\n" +
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
            "      %s\n" +
            "    ]\n" +
            "    auto-down-unreachable-after = 10s\n" +
            "    roles = [ %s ]\n" +
            "  }\n" +
            "}";


    @Test
    public void loadConfigTest() {
        AkkaConfigurationLoader loader = new AkkaConfigurationLoader();
        String config = loader.loadConfigurationTemplate();
        assertThat(config).isNotNull();
        assertThat(config).isEqualTo(EXPECTED_CONFIG);
    }
}