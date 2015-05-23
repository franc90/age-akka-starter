package org.age.akka;

import akka.actor.ActorSystem;
import org.age.akka.structures.AkkaNodeConfig;

public class AkkaUtils {

    private static ActorSystem actorSystem;
    private static AkkaNodeConfig config;

    public static ActorSystem getActorSystem() {
        return actorSystem;
    }

    public static void setActorSystem(ActorSystem actorSystem) {
        AkkaUtils.actorSystem = actorSystem;
    }

    public static AkkaNodeConfig getConfig() {
        return config;
    }

    public static void setConfig(AkkaNodeConfig config) {
        AkkaUtils.config = config;
    }
}
