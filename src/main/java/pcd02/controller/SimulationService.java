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
    private final StopFlag stopFlag;
    private final int poolSize = Runtime.getRuntime().availableProcessors() + 1;
    List<List<Body>> bodiesSplit;
    private static final double FPS = 60;


    public SimulationService(SimulationState state, int numberOfSteps, View view, StartSynch startSynch, StopFlag stopFlag) {
        this.executor = Executors.newCachedThreadPool();
        this.state = state;
        this.taskFactory = new TaskFactory();
        this.view = view;
        this.startSynch = startSynch;
        this.stopFlag = stopFlag;
        this.numberOfSteps = numberOfSteps;
        this.bodiesSplit = Lists.partition(state.getBodies(), state.getBodies().size() / this.poolSize +1);
    }

    public void run() {

        startSynch.waitStart();
        Chrono time = new Chrono();
        time.start();
        while (state.getSteps() < this.numberOfSteps) {
            long initialTime = System.currentTimeMillis();
            if (stopFlag.isSet()) {
                startSynch.waitStart();
            }

            List<Future<List<Body>>> results = new LinkedList<>();

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
            double elapsed = System.currentTimeMillis() - initialTime;
            if (elapsed < (( 1 / FPS) * 1000)) {
                try {
                    Thread.sleep((long) (((1 / FPS) * 1000) - elapsed));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        time.stop();
        System.out.println("Time elapsed: " + time.getTime());
        System.exit(0);
    }
}
