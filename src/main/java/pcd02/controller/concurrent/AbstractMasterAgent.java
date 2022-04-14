package pcd02.controller.concurrent;

import pcd02.model.SimulationState;
import pcd02.model.concurrent.AbstractTaskFactory;
import pcd02.model.concurrent.TaskBag;
import pcd02.utils.Chrono;
import pcd02.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 *  This class is implemented as an Abstract Class to make it easier manage two different implementation of Master Agent.
 *  Despite this, at every simulation only one of them will be executed.
 */
public abstract class AbstractMasterAgent extends Thread{
    private final long maxSteps;
    private final View view;
    private final StopFlag stopFlag;
    private final StartSynch startSynch;
    private final List<WorkerAgent> workers;
    protected SimulationState state;
    protected int nWorker;
    protected TaskBag taskBag;
    protected TaskCompletionLatch taskLatch;
    protected AbstractTaskFactory taskFactory;
    private static final double FPS = 120;


    public AbstractMasterAgent(View view, SimulationState state, AbstractTaskFactory taskFactory, long maxSteps, StopFlag stopFlag, StartSynch startSynch) {
        this.state = state;
        this.maxSteps = maxSteps;
        this.view = view;
        this.taskFactory = taskFactory;
        this.nWorker = Runtime.getRuntime().availableProcessors() + 1;
        this.taskBag = new TaskBag();
        this.stopFlag = stopFlag;
        this.startSynch = startSynch;
        this.workers = new ArrayList<>(nWorker);
    }

    public void run() {

        Chrono time = new Chrono();
        startSynch.waitStart();
        this.createWorkerAgents();
        time.start();
        while( state.getSteps() < maxSteps ) {
            long initialTime = System.currentTimeMillis();
            if (stopFlag.isSet()) {
                startSynch.waitStart();
            }

            this.addComputeForcesTasksToBag();
            this.waitStepDone();

            this.addUpdatePositionTasksToBag();
            this.waitStepDone();

            state.setVt(state.getVt() + state.getDt());
            view.display(state);
            state.incrementSteps();

            double elapsed = System.currentTimeMillis() - initialTime;
            if (elapsed < ((1 / FPS)*1000)) {
                try {
                    Thread.sleep((long) (((1 / FPS)*1000) - elapsed));
                } catch(Exception ignored){}
            }
        }
        time.stop();
        System.out.println("Time elapsed: " + time.getTime() + " ms.");
        // Gracefully kill the execution of worker agents.
        for (WorkerAgent worker : this.workers) {
            worker.interrupt();
        }
    }

    private void waitStepDone() {
        try {
            taskLatch.waitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.taskLatch.reset();
    }

    private void createWorkerAgents() {
        IntStream.range(0, nWorker).forEach(a -> this.workers.add(new WorkerAgent(taskBag, taskLatch, stopFlag, startSynch)));
        this.workers.forEach(Thread::start);
    }

    abstract void addComputeForcesTasksToBag();
    abstract void addUpdatePositionTasksToBag();
}
