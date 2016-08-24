package org.age.akka.core.actors.master;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.age.akka.core.actors.master.services.LifecycleServiceActor;
import org.age.akka.core.actors.master.services.TaskServiceActor;
import org.age.akka.core.actors.master.services.TopologyServiceActor;
import org.age.akka.core.actors.master.services.WorkerServiceActor;
import org.age.akka.core.helper.TimeUtils;
import org.age.akka.core.messages.lifecycle.LifecycleUpdatedRequest;
import org.age.akka.core.messages.task.UpdateTaskStateRequest;
import org.age.akka.core.messages.task.UpdateTaskStateResponse;
import org.age.akka.core.messages.topology.TopologyUpdateRequest;
import org.age.akka.core.messages.topology.TopologyUpdateResponse;
import org.age.akka.core.messages.worker.lifecycle.AddWorkerFailedResponse;
import org.age.akka.core.messages.worker.lifecycle.AddWorkerRequest;
import org.age.akka.core.messages.worker.lifecycle.AddWorkerSucceededResponse;
import org.age.akka.core.messages.worker.lifecycle.RemoveWorkerRequest;
import org.age.akka.core.messages.worker.lifecycle.UpdateWorkersRequest;
import org.age.akka.core.messages.worker.topology.UpdateWorkerTopologiesRequest;
import org.age.akka.core.messages.worker.topology.UpdateWorkerTopologiesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MasterActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private static final Logger logger = LoggerFactory.getLogger(MasterActor.class);

    private ActorRef topologyService;

    private ActorRef workerService;

    private ActorRef taskService;

    private Queue<LifecycleUpdatedRequest> lifecycleUpdateRequests = new ConcurrentLinkedQueue<>();

    public MasterActor() {
        receive(ReceiveBuilder
                .match(LifecycleUpdatedRequest.class, this::processLifecycleUpdatedRequest)
                .match(AddWorkerSucceededResponse.class, this::processAddWorkerSucceededResponse)
                .match(AddWorkerFailedResponse.class, this::processAddWorkerFailedResponse)
                .match(UpdateTaskStateResponse.class, this::processTaskState)
                .match(TopologyUpdateResponse.class, this::processTopologyUpdateResponse)
                .match(UpdateWorkerTopologiesResponse.class, this::processUpdateWorkerTopologiesResponse)
                .matchAny(msg -> log.info("Received not supported message {}", msg))
                .build());
    }

    @Override
    public void preStart() throws Exception {
        workerService = context().actorOf(Props.create(WorkerServiceActor.class), "workerService");
        topologyService = context().actorOf(Props.create(TopologyServiceActor.class), "topologyService");
        taskService = context().actorOf(Props.create(TaskServiceActor.class), "taskService");
        ActorRef lifecycleService = context().actorOf(Props.create(LifecycleServiceActor.class), "lifecycleService");
        context().watch(workerService);
        context().watch(topologyService);
        context().watch(taskService);
        context().watch(lifecycleService);
    }

    private void processLifecycleUpdatedRequest(LifecycleUpdatedRequest request) throws Exception {
        log.debug("lifecycle updated: {}", request);

        lifecycleUpdateRequests.add(request);
        if (lifecycleUpdateRequests.size() > 1) {
            log.info("already processing lifecycle update. Adding {} to queue", request);
            return;
        }

        updateWorkerState(request);
    }

    private void updateWorkerState(LifecycleUpdatedRequest request) throws Exception {
        log.debug("update update worker based on {}", request);
        UpdateWorkersRequest updateWorker = prepareUpdateWorkerRequest(request);
        workerService.tell(updateWorker, self());
    }

    private UpdateWorkersRequest prepareUpdateWorkerRequest(LifecycleUpdatedRequest request) {
        if (request.getType() == LifecycleUpdatedRequest.Type.REMOVE) {
            return new RemoveWorkerRequest(request.getAddress());
        }
        return new AddWorkerRequest(request.getAddress());
    }

    private void processAddWorkerSucceededResponse(AddWorkerSucceededResponse response) {
        log.debug("worker {} added", response.getAddedActorId());
        taskService.tell(new UpdateTaskStateRequest(UpdateTaskStateRequest.Type.PAUSE), self());
    }

    private void processAddWorkerFailedResponse(AddWorkerFailedResponse response) throws Exception {
        log.debug("adding {} failed", response.getFailedWorkerId());
        processUpdateWorkerTopologiesResponse(null);
    }

    private void processTaskState(UpdateTaskStateResponse taskTaskState) {
        log.debug("task state updated");
        if (taskTaskState.getState() == UpdateTaskStateResponse.State.PAUSED) {
            topologyService.tell(new TopologyUpdateRequest(), self());
            return;
        }
        log.error("task not paused");
    }

    private void processTopologyUpdateResponse(TopologyUpdateResponse response) {
        log.debug("received updated topology {}", response.getTopology());
        workerService.tell(new UpdateWorkerTopologiesRequest(response.getTopology()), self());
    }

    private void processUpdateWorkerTopologiesResponse(UpdateWorkerTopologiesResponse response) throws Exception {
        lifecycleUpdateRequests.poll();
        if (lifecycleUpdateRequests.isEmpty()) {
            log.debug("no more lifecycle update request - resume tasks if paused");
            taskService.tell(new UpdateTaskStateRequest(UpdateTaskStateRequest.Type.RESUME), self());
            return;
        }

        log.debug("more lifecycle update requests queued, update nodes membership");
        LifecycleUpdatedRequest lifecycleUpdatedRequest = lifecycleUpdateRequests.peek();
        updateWorkerState(lifecycleUpdatedRequest);
    }
}
