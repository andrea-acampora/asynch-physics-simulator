package pcd02.model.concurrent;

import pcd02.model.Body;
import pcd02.model.SimulationState;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * The factory responsible to create different types of tasks.
 */
public interface AbstractTaskFactory {

    /**
     *
     * @param state The current state of the simulation.
     * @param bodiesList The set of {@link Body} of the simulation.
     * @return the {@link Task} used to compute the forces of a body.
     */
    Runnable createComputeForcesTask(SimulationState state, Body b);

    /**
     *
     * @param state The current state of the simulation.
     * @param bodiesList The set of {@link Body} of the simulation.
     * @return the {@link Task} used to update the position of a body.
     */
    Runnable createUpdatePositionTask(SimulationState state, Body b);
}
