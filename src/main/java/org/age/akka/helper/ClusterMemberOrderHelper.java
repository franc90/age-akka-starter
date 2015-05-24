package org.age.akka.helper;

import akka.actor.Address;
import org.age.akka.AkkaUtils;
import org.age.akka.exceptions.NoMemberInClusterException;
import org.age.akka.structures.AkkaNode;

import java.util.List;

public class ClusterMemberOrderHelper {

    public static int getOrder(Address memberAddress) {
        List<AkkaNode> clusterNodes = AkkaUtils.getConfig().getSeedNodes();
        String host = memberAddress.host().get();
        int port = (Integer) memberAddress.port().get();

        for (int i = 0; i < clusterNodes.size(); i++) {
            AkkaNode akkaNode = clusterNodes.get(i);

            if (akkaNode.getHostname().equals(host) && akkaNode.getPort() == port) {
                return i;
            }
        }

        throw new NoMemberInClusterException("Member " + host + ":" + port + " not in cluster.");
    }
}
