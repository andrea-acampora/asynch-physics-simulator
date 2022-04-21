package pcd02.model.concurrent;

import pcd02.model.Body;
import pcd02.model.SimulationState;
import pcd02.model.V2d;

import java.util.List;
import java.util.concurrent.Callable;

public class TaskFactory implements AbstractTaskFactory {

    @Override
    public Callable<List<Body>> createComputeForcesTask(SimulationState state, List<Body> bodies) {
        return () -> {
                for (Body b : bodies){
                    V2d totalForce = new V2d(0, 0);
                    /* compute total repulsive force */
                    for (int j = 0; j < state.getBodies().size(); j++) {
                        Body otherBody = state.getBodies().get(j);
                        if (!b.equals(otherBody)) {
                            try {
                                V2d forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                                totalForce.sum(forceByOtherBody);
                            } catch (Exception ignored) {}
                        }
                    }
                    /* add friction force */
                    totalForce.sum(b.getCurrentFrictionForce());
                    /* compute instant acceleration */
                    V2d acc = new V2d(totalForce).scalarMul(1.0 / b.getMass());
                    /* update velocity */
                    b.updateVelocity(acc, state.getDt());
                }
                return bodies;
        };
    }

    @Override
    public Callable<List<Body>> createUpdatePositionTask(SimulationState state, List<Body> bodies) {
        return () -> {
            for (Body b : bodies) {
                /* update bodies new pos */
                b.updatePos(state.getDt());
                b.checkAndSolveBoundaryCollision(state.getBounds());
            }
            return bodies;
        };
    }
}
