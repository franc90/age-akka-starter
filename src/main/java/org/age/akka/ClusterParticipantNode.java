package org.age.akka;

import org.age.akka.start.cluster.participant.ClusterParticipantNodeStarter;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.UnknownHostException;

@Named
public class ClusterParticipantNode {

    @Inject
    private ClusterParticipantNodeStarter clusterClientStarter;

    public void startWork() throws InterruptedException, UnknownHostException {
        clusterClientStarter.startWork();

        System.out.println("\n\n\nWORKING");
    }


}
