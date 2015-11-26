package org.age.akka;

import org.age.akka.start.ClusterNodeStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.UnknownHostException;

public class ClusterNode {

    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);
    private ClusterNodeStarter clusterClientStarter;

    public ClusterNode() {
        ApplicationContext context = new ClassPathXmlApplicationContext("akka/config/app.cfg.xml");
        clusterClientStarter = context.getBean(ClusterNodeStarter.class);
    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        ClusterNode client = new ClusterNode();

        client.startWork();
    }

    private void startWork() throws InterruptedException, UnknownHostException {
        clusterClientStarter.startWork();
    }


}
