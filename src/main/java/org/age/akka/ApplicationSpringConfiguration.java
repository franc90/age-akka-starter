package org.age.akka;

import com.google.common.eventbus.EventBus;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MembershipListener;
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

    @Bean
    public HazelcastInstance hazelcastInstance(MembershipListener membershipListener) {
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

        config.addListenerConfig(new ListenerConfig(membershipListener));

        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

}
