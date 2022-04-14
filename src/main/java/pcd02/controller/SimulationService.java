package pcd02.controller;

import pcd02.controller.concurrent.StartSynch;
import pcd02.model.Body;
import pcd02.model.P2d;
import pcd02.model.SimulationState;
import pcd02.model.V2d;
import pcd02.model.concurrent.AbstractTaskFactory;
import pcd02.model.concurrent.TaskFactory;
import pcd02.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimulationService extends Thread{

    private int poolSize = 8;
    private ExecutorService executor;
    protected SimulationState state;
    private int maxIter = 500;
    private AbstractTaskFactory taskFactory;
    private View view;
    private StartSynch startSynch;

    public SimulationService(SimulationState state, View view, StartSynch startSynch) {
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.state = state;
        this.taskFactory = new TaskFactory();
        this.view = view;
        this.startSynch = startSynch;
    }

    public void start() {
        startSynch.waitStart();
        while(state.getSteps() < maxIter){
            //mi creao una nuova lista
            List<Future<Body>> results = new LinkedList<>();
            List<Future<Body>> results2 = new LinkedList<>();

            //calcolo la velocità di tutti i body
//            ArrayList<Body> defCopy = new ArrayList<>();
//            state.getBodies().forEach(b -> defCopy.add(new Body(b.getId(), new P2d(b.getPos().getX(), b.getPos().getY()), new V2d(b.getVel().x, b.getVel().y), b.getMass())));
//
            state.getBodies().forEach(b -> {
                executor.submit(taskFactory.createComputeForcesTask(state, b));
            });

            state.getBodies().forEach(b -> {
              executor.submit(taskFactory.createUpdatePositionTask(state, b));
            });

            view.display(state);
            state.incrementSteps();
            state.setVt(state.getVt() + state.getDt());
        }
    }
}
