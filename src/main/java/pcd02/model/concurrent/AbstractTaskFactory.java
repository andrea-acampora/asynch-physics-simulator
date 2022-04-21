package pcd02.model.concurrent;

import pcd02.model.Body;
import pcd02.model.SimulationState;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * The factory responsible to create different types of tasks.
 */
public interface AbstractTaskFactory {

    Callable<List<Body>> createComputeForcesTask(SimulationState state, List<Body> bodies);

    Callable<List<Body>> createUpdatePositionTask(SimulationState state, List<Body> bodies);
}
