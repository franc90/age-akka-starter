package org.age.akka.core.data;

import akka.actor.ActorSystem;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class StartedCluster {

    private final ActorSystem actorSystem;

    private final boolean clusterStarted;

    StartedCluster(ActorSystem actorSystem, boolean clusterStarted) {
        this.actorSystem = actorSystem;
        this.clusterStarted = clusterStarted;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    public boolean isClusterStarted() {
        return clusterStarted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartedCluster that = (StartedCluster) o;
        return clusterStarted == that.clusterStarted &&
                Objects.equal(actorSystem, that.actorSystem);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(actorSystem, clusterStarted);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("actorSystem", actorSystem)
                .add("clusterStarted", clusterStarted)
                .toString();
    }
}
