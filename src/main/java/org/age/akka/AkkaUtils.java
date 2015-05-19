package org.age.akka;

import akka.actor.ActorSystem;

public class AkkaUtils {

    private static ActorSystem actorSystem;

    public static ActorSystem getActorSystem() {
        return actorSystem;
    }

    public static void setActorSystem(ActorSystem actorSystem) {
        AkkaUtils.actorSystem = actorSystem;
    }
}
