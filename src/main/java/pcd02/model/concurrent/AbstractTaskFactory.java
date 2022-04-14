package pcd02.model.concurrent;

import pcd02.model.Body;
import pcd02.model.SimulationState;

import java.util.List;

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
    Task createComputeForcesTask(SimulationState state, List<Body> bodiesList);

    /**
     *
     * @param state The current state of the simulation.
     * @param bodiesList The set of {@link Body} of the simulation.
     * @return the {@link Task} used to update the position of a body.
     */
    Task createUpdatePositionTask(SimulationState state, List<Body> bodiesList);
}
