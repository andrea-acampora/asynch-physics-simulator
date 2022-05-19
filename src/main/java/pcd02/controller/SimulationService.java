package pcd02.controller;

import com.google.common.collect.Lists;
import pcd02.controller.concurrent.StartSynch;
import pcd02.controller.concurrent.StopFlag;
import pcd02.model.Body;
import pcd02.model.SimulationState;
import pcd02.model.concurrent.AbstractTaskFactory;
import pcd02.model.concurrent.TaskFactory;
import pcd02.utils.Chrono;
import pcd02.view.View;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class SimulationService extends Thread {

    private final ExecutorService executor;
    private final SimulationState state;
    private final int numberOfSteps;
    private final AbstractTaskFactory taskFactory;
    private final View view;
    private final StartSynch startSynch;
    private final StopFlag stopFlag;
    private final List<List<Body>> bodiesSplit;


    public SimulationService(SimulationState state, int numberOfSteps, View view, StartSynch startSynch, StopFlag stopFlag, AbstractTaskFactory taskFactory) {

        this.state = state;
        this.taskFactory = taskFactory;
        this.view = view;
        this.startSynch = startSynch;
        this.stopFlag = stopFlag;
        this.numberOfSteps = numberOfSteps;
        int poolSize = Runtime.getRuntime().availableProcessors() + 1;
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.bodiesSplit = Lists.partition(state.getBodies(), state.getBodies().size() / poolSize + 1 );
    }

    public void run() {

        startSynch.waitStart();
        Chrono time = new Chrono();
        time.start();
        while (state.getSteps() < this.numberOfSteps) {
            if (stopFlag.isSet()) {
                startSynch.waitStart();
            }

            List<Future<List<Void>>> results = new LinkedList<>();

            bodiesSplit.forEach(split -> results.add(executor.submit(taskFactory.createComputeForcesTask(state, split))));
            results.forEach(a -> {
                try {
                    a.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

            results.clear();
            bodiesSplit.forEach(split -> results.add(executor.submit(taskFactory.createUpdatePositionTask(state, split))));

            results.forEach(a -> {
                try {
                    a.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            results.clear();
            state.setVt(state.getVt() + state.getDt());
            view.display(state);
            state.incrementSteps();
        }
        time.stop();
        System.out.println("Time elapsed: " + time.getTime());
        System.exit(0);
    }
}
