package org.age.akka.start.utils;

import akka.actor.ActorSystem;

import javax.inject.Named;

@Named
public class ClusterDataHolder {

    private ActorSystem actorSystem;

    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    public void setActorSystem(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }
}
