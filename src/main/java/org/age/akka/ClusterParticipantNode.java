package org.age.akka;

import org.age.akka.start.cluster.participant.ClusterParticipantNodeStarter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.UnknownHostException;

public class ClusterParticipantNode {

    private ClusterParticipantNodeStarter clusterClientStarter;

    private ClusterParticipantNode() {
        ApplicationContext context = new ClassPathXmlApplicationContext("akka/config/app.cfg.xml");
        clusterClientStarter = context.getBean(ClusterParticipantNodeStarter.class);
    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        ClusterParticipantNode client = new ClusterParticipantNode();

        client.startWork();
    }

    private void startWork() throws InterruptedException, UnknownHostException {
        clusterClientStarter.startWork();
    }


}
