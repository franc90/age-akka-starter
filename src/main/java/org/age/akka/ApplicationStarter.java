package org.age.akka;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;

@SpringBootApplication
public class ApplicationStarter implements CommandLineRunner {

    @Inject
    private ClusterManager clusterManager;

    @Inject
    private ClusterParticipantNode participantNode;

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0 || "worker".equals(args[0])) {
            participantNode.startWork();
        } else if ("manager".equals(args[0])) {
            clusterManager.startWork();
        } else {
            System.out.println("Required:");
            System.out.println("'' or 'worker' - runs application as cluster participant");
            System.out.println("'manager' - runs application as cluster manager");
        }
    }
}
