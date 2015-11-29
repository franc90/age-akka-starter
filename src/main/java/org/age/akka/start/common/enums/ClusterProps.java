package org.age.akka.start.common.enums;

import org.age.akka.start.common.data.ActorSystemName;
import org.age.akka.start.common.data.Role;

public interface ClusterProps {

    ActorSystemName ACTOR_SYSTEM_NAME = new ActorSystemName("age3");

    Role CLUSTER_MEMBER = new Role("cluster");

    Role WORKER = new Role("worker");
}
