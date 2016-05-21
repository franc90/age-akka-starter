package org.age.akka.config;

import com.google.common.eventbus.EventBus;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.TcpIpConfig;
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
@PropertySource("classpath:cluster.properties")
public class SpringConfiguration {

    @Value("#{'${network.tcp.members:127.0.0.1}'.split(',')}")
    private List<String> tcpMembers;

    @Bean
    public Config hazelcastConfig(MembershipListener membershipListener) {
        Config config = new Config();

        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);

        TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
        tcpIpConfig.setEnabled(true);
        tcpMembers.forEach(tcpIpConfig::addMember);

        config.getProperties().put("hazelcast.logging.type", "slf4j");
        config.getProperties().put("hazelcast.shutdownhook.enabled", "false");

        config.addListenerConfig(new ListenerConfig(membershipListener));

        return config;
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
