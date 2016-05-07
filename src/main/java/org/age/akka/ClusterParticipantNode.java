package org.age.akka;

import org.age.akka.start.cluster.participant.ClusterParticipantNodeStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.UnknownHostException;

public class ClusterParticipantNode {

    @Autowired
    private ClusterParticipantNodeStarter clusterClientStarter;

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationSpringConfiguration.class);
        ClusterParticipantNode client = context.getBean("clusterParticipantNode", ClusterParticipantNode.class);

        client.startWork();
    }

    private void startWork() throws InterruptedException, UnknownHostException {
        clusterClientStarter.startWork();
    }


}
