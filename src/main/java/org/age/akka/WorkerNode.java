package org.age.akka;

import org.age.akka.start.startup.worker.WorkerNodeStarter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.UnknownHostException;

public class WorkerNode {

    private WorkerNodeStarter clusterClientStarter;

    public WorkerNode() {
        ApplicationContext context = new ClassPathXmlApplicationContext("akka/config/app.cfg.xml");
        clusterClientStarter = context.getBean(WorkerNodeStarter.class);
    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        WorkerNode client = new WorkerNode();

        client.startWork();
    }

    private void startWork() throws InterruptedException, UnknownHostException {
        clusterClientStarter.startWork();
    }


}
