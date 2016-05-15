package org.age.akka.core;

import akka.actor.ActorSystem;
import org.age.akka.start.common.data.ClusterConfigHolder;

public class AkkaUtils {

    private static ActorSystem actorSystem;

    private static ClusterConfigHolder config;

    public static ActorSystem getActorSystem() {
        return actorSystem;
    }

    public static void setActorSystem(ActorSystem actorSystem) {
        AkkaUtils.actorSystem = actorSystem;
    }

    public static ClusterConfigHolder getConfig() {
        return config;
    }

    public static void setConfig(ClusterConfigHolder config) {
        AkkaUtils.config = config;
    }
}
