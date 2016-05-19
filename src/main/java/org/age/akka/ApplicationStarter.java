package org.age.akka;

import org.age.akka.start.cluster.ClusterManager;
import org.age.akka.start.cluster.ClusterParticipantNode;
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
        System.out.println("\n\nAKKA CLUSTER STARTER:");
        if (args.length == 0 || "worker".equals(args[0])) {
            System.out.println("Starting worker\n");
            participantNode.startWork();
        } else if ("manager".equals(args[0])) {
            System.out.println("Starting manager\n");
            clusterManager.startWork();
        } else {
            System.out.println("Required start argument:");
            System.out.println("'' or 'worker' - runs application as cluster participant");
            System.out.println("'manager' - runs application as cluster manager\n\n");
            System.exit(1);
        }
    }
}
