package org.age.akka.core;

import akka.actor.ActorSystem;
import org.age.akka.start.common.utils.ClusterDataHolder;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class WorkerCreator {

    @Inject
    private ClusterDataHolder clusterDataHolder;

    private ActorSystem actorSystem;

    public synchronized ActorSystem createActorSystem() {
        if (clusterDataHolder.isWorkerCreated()) {
            return actorSystem;
        }

        // TODO: crate worker
        clusterDataHolder.setWorkerCreated(true);
        return null;
    }
}
