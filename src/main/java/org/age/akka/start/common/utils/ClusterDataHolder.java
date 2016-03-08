package org.age.akka.start.common.utils;

import akka.actor.ActorSystem;

import javax.inject.Named;

@Named("org.age.akka.start.common.utils.ClusterDataHolder")
public class ClusterDataHolder {

    private ActorSystem actorSystem;

    private boolean isWorkerCreated;

    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    public void setActorSystem(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public boolean isWorkerCreated() {
        return isWorkerCreated;
    }

    public void setWorkerCreated(boolean workerCreated) {
        isWorkerCreated = workerCreated;
    }
}
