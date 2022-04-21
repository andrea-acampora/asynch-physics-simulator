package pcd02.model.concurrent;

import pcd02.model.Body;
import pcd02.model.SimulationState;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * The factory responsible to create different types of tasks.
 */
public interface AbstractTaskFactory {

    Callable<Body> createComputeForcesTask(SimulationState state, Body b);

    Callable<Body> createUpdatePositionTask(SimulationState state, Body b);
}
