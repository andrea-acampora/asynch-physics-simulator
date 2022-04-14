package pcd02.controller.concurrent;

import com.google.common.collect.Lists;
import pcd02.model.Body;
import pcd02.model.SimulationState;
import pcd02.model.concurrent.AbstractTaskFactory;
import pcd02.model.concurrent.Task;
import pcd02.view.View;
import java.util.List;
import java.util.stream.IntStream;

/**
 * In this implementation of Master Agent every {@link Task} manage a sub-list of {@link Body} of the simulation.
 */
public class MasterAgentSubListPerTask extends AbstractMasterAgent {
    private final List<List<Body>> bodiesSplit;

    public MasterAgentSubListPerTask(View view, SimulationState state, AbstractTaskFactory taskFactory, long maxSteps, StopFlag stopFlag, StartSynch startSynch) {
        super(view, state,taskFactory, maxSteps, stopFlag, startSynch);
        this.bodiesSplit = Lists.partition(state.getBodies(),  state.getBodies().size() / nWorker +1);
        this.taskLatch = new TaskCompletionLatch(nWorker);
    }

    @Override
    void addComputeForcesTasksToBag() {
        IntStream.range(0, nWorker).forEach((i) -> taskBag.addNewTask(taskFactory.createComputeForcesTask(state, bodiesSplit.get(i))));
    }

    @Override
    void addUpdatePositionTasksToBag() {
        IntStream.range(0, nWorker).forEach((i) -> taskBag.addNewTask(taskFactory.createUpdatePositionTask(state, bodiesSplit.get(i))));
    }
}
