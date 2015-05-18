package org.age.akka;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SimpleClusterApp {

    public static void main(String[] args) {
        if (args.length == 0)
            startup(new String[]{"2551", "2552", "0"});
        else
            startup(args);
    }

    private static void startup(String[] ports) {
        String hostname = "127.0.0.1";
        for (String port : ports) {
            Config config = ConfigFactory.parseString(
                    "akka {\n" +
                            "  actor {\n" +
                            "    provider = \"akka.cluster.ClusterActorRefProvider\"\n" +
                            "  }\n" +
                            "  remote {\n" +
                            "    log-remote-lifecycle-events = off\n" +
                            "    netty.tcp {\n" +
                            "      hostname = \"" + hostname + "\"\n" +
                            "      port = " + port + "\n" +
                            "    }\n" +
                            "  }\n" +
                            "\n" +
                            "  cluster {\n" +
                            "    seed-nodes = [\n" +
                            "      \"akka.tcp://ClusterSystem@127.0.0.1:2551\",\n" +
                            "      \"akka.tcp://ClusterSystem@127.0.0.1:2552\"\n" +
                            "    ]\n" +
                            "    auto-down-unreachable-after = 10s\n" +
                            "  }\n" +
                            "}\n");

            // Create an Akka system
            ActorSystem system = ActorSystem.create("ClusterSystem", config);

            // Create an actor that handles cluster domain events
            system.actorOf(Props.create(SimpleClusterListener.class),
                    "clusterListener");

        }
    }

}
