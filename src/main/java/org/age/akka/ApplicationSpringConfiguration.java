package org.age.akka;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.List;

@Configuration
@ComponentScan("org.age.akka")
@PropertySource("classpath:application.properties")
public class ApplicationSpringConfiguration {

    @Value("#{'${network.tcp.members:127.0.0.1}'.split(',')}")
    private List<String> tcpMembers;

    @Value("${cluster.minimal.clients:3}")
    private int minimalClusterClients;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        NetworkConfig networkConfig = config.getNetworkConfig();

        JoinConfig join = networkConfig.getJoin();
        MulticastConfig multicastConfig = join.getMulticastConfig();
        multicastConfig.setEnabled(false);

        TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
        tcpIpConfig.setEnabled(true);
        tcpMembers.forEach(tcpIpConfig::addMember);

        config.getProperties().put("hazelcast.logging.type", "slf4j");
        config.getProperties().put("hazelcast.shutdownhook.enabled", "false");

        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public ClusterManager clusterManager() {
        return new ClusterManager(minimalClusterClients);
    }

    @Bean
    public ClusterParticipantNode clusterParticipantNode() {
        return new ClusterParticipantNode();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
