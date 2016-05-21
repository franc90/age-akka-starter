package org.age.akka.start.common.utils;

import com.hazelcast.core.IAtomicReference;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.concurrent.atomic.AtomicBoolean;

@Named
public class MasterUtils extends HazelcastBean {

    private IAtomicReference<String> masterUUID;

    private final AtomicBoolean master = new AtomicBoolean(true);

    @PostConstruct
    public void init() {
        masterUUID = getHazelcastInstance().getAtomicReference("masterUUID");
    }

    public boolean isMaster() {
        return master.get();
    }

    public boolean isNotMaster() {
        return !isMaster();
    }

    public void setMaster() {
        master.set(true);
    }

    public IAtomicReference<String> getMasterUUID() {
        return masterUUID;
    }
}
