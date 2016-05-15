package org.age.akka.start.common.utils;

import akka.actor.ActorSystem;

import javax.inject.Named;
import java.util.concurrent.atomic.AtomicBoolean;

@Named("org.age.akka.start.common.utils.ClusterDataHolder")
public class ClusterDataHolder {

    private final AtomicBoolean creatingWorker = new AtomicBoolean();

    private ActorSystem actorSystem;

    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    public void setActorSystem(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public boolean getCreatingWorker() {
        return creatingWorker.getAndSet(true);
    }

}
