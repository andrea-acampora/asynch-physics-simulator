package pcd02.controller.concurrent;

import pcd02.model.*;
import pcd02.model.concurrent.AbstractTaskFactory;
import pcd02.model.concurrent.Task;
import pcd02.view.View;
import java.util.Collections;

/**
 * In this implementation of Master Agent every {@link Task} manage a single {@link Body} of the simulation.
 */
public class MasterAgentOneBodyPerTask extends AbstractMasterAgent {

    public MasterAgentOneBodyPerTask(View view, SimulationState state, AbstractTaskFactory taskFactory, long maxSteps, StopFlag stopFlag, StartSynch startSynch) {
        super(view, state, taskFactory, maxSteps, stopFlag, startSynch);
        this.taskLatch = new TaskCompletionLatch(state.getBodies().size());
    }

    @Override
    void addComputeForcesTasksToBag() {
        state.getBodies().forEach( b -> taskBag.addNewTask(taskFactory.createComputeForcesTask(state, Collections.singletonList(b))));
    }

    @Override
    void addUpdatePositionTasksToBag() {
        state.getBodies().forEach( b -> taskBag.addNewTask(taskFactory.createUpdatePositionTask(state, Collections.singletonList(b))));
    }
}
