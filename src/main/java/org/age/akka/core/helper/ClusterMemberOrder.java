package org.age.akka.core.helper;

import akka.actor.Address;
import org.age.akka.core.AkkaUtils;
import org.age.akka.core.exceptions.NoMemberInClusterException;
import org.age.akka.start.common.data.AkkaNode;

import java.util.List;

public class ClusterMemberOrder {

    public int getOrder(Address memberAddress) {
        List<AkkaNode> clusterNodes = AkkaUtils.getConfig().getClusterNodes();
        String host = memberAddress.host().get();
        int port = (Integer) memberAddress.port().get();

        for (int i = 0; i < clusterNodes.size(); i++) {
            AkkaNode akkaNode = clusterNodes.get(i);

            if (akkaNode.getHostname().getHostname().equals(host) && akkaNode.getPort().getPort() == port) {
                return i;
            }
        }

        throw new NoMemberInClusterException("Member " + host + ":" + port + " not in cluster.");
    }
}
