package pcd02.controller.concurrent;

import pcd02.model.concurrent.Task;
import pcd02.model.concurrent.TaskBag;

/**
 * The agent responsible to execute the computation of the simulation.
 * It continuously tries to get a {@link Task} from a {@link TaskBag} and executes it.
 * It also notifies the end of the execution of the task to a {@link TaskCompletionLatch}.
 */
public class WorkerAgent extends Thread {

    private final TaskBag bag;
    private final TaskCompletionLatch latch;
    private final StopFlag stopFlag;
    private final StartSynch startSynch;

    public WorkerAgent(TaskBag bag, TaskCompletionLatch latch, StopFlag stopFlag, StartSynch startSynch) {
        this.bag = bag;
        this.latch = latch;
        this.stopFlag = stopFlag;
        this.startSynch = startSynch;
    }

    @Override
    public void run() {
        while (true) {
            try{
                if (stopFlag.isSet()) {
                    startSynch.waitStart();
                }
                Task task = bag.getATask();
                task.computeTask();
                latch.notifyCompletion();
            } catch (InterruptedException ignored){}
        }
    }
}
