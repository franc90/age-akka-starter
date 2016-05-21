package org.age.akka;

import org.age.akka.start.cluster.ClusterNode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;

@SpringBootApplication
public class ApplicationStarter implements CommandLineRunner {

    @Inject
    private ClusterNode clusterNode;

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n\nAKKA CLUSTER STARTER:");
        clusterNode.startWork();
    }
}
