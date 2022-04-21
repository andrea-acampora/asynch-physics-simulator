package pcd02.controller;

import pcd02.controller.concurrent.StartSynch;
import pcd02.controller.concurrent.StopFlag;
import pcd02.model.Body;
import pcd02.model.P2d;
import pcd02.model.SimulationState;
import pcd02.model.V2d;
import pcd02.model.concurrent.AbstractTaskFactory;
import pcd02.model.concurrent.TaskFactory;
import pcd02.utils.Chrono;
import pcd02.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimulationService extends Thread {

    private final ExecutorService executor;
    protected SimulationState state;
    private final int numberOfSteps;
    private final AbstractTaskFactory taskFactory;
    private final View view;
    private final StartSynch startSynch;
    private StopFlag stopFlag;

    public SimulationService(SimulationState state, int numberOfSteps, View view, StartSynch startSynch, StopFlag stopFlag) {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        this.state = state;
        this.taskFactory = new TaskFactory();
        this.view = view;
        this.startSynch = startSynch;
        this.stopFlag = stopFlag;
        this.numberOfSteps = numberOfSteps;
    }

    public void run() {

        startSynch.waitStart();

        Chrono time = new Chrono();
        time.start();
        while(state.getSteps() < this.numberOfSteps) {
            if(stopFlag.isSet()) {
                startSynch.waitStart();
            }

            List<Future<Body>> results = new LinkedList<>();

            state.getBodies().forEach(b -> results.add(executor.submit(taskFactory.createComputeForcesTask(state, b))));

            results.forEach(a -> {
                try {
                    a.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

            results.clear();

            state.getBodies().forEach(b -> results.add(executor.submit(taskFactory.createUpdatePositionTask(state, b))));

            results.forEach(a -> {
                try {
                    a.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

            results.clear();
            view.display(state);
            state.incrementSteps();
            state.setVt(state.getVt() + state.getDt());
        }
        time.stop();
        System.out.println("Time elapsed: " + time.getTime());
    }
}
