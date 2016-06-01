#age-akka-starter

An Akka cluster starter created using Hazelcast and Spring frameworks. It is a POC to be used as a part of age3 (a multi-agent computation framework developed at AGH UST)


#parameters
Application allows for some basic configuration.  

Properties and default values:
```
cluster.minimal.clients=1 - minimal number of nodes to start computation cluster

network.use.multicast.discovery=false - if false application uses remaining network properties to set up tcp connection 
network.tcp.members=127.0.0.1
network.interface.names=eth0,lo

computation.task=org.age.akka.core.actors.worker.task.SimpleTaskActor
```

Available computation tasks:
```
org.age.akka.core.actors.worker.task.SimpleTaskActor
org.age.akka.core.actors.worker.task.SimpleLongRunningTaskActor
org.age.akka.core.actors.worker.task.SimpleBroadcastCommunicationTaskActor
org.age.akka.core.actors.worker.task.SoutTaskActor
```

#usage
to create runnable jar file, execute: ./gradlew bootRepackage  
Spring boot allows using generated jar file as bash script - one needs only to chmod +x.

example:
```
./age-akka-0.0.1-SNAPSHOT.jar --cluster.minimal.clients=1
java -jar age-akka-0.0.1-SNAPSHOT.jar --computation.task=org.age.akka.core.actors.worker.task.SimpleLongRunningTaskActor --cluster.minimal.clients=1
 ```