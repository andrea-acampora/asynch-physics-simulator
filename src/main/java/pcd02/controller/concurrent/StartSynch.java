package pcd02.controller.concurrent;

/**
 * Implemented as monitor and used to synchronize the {@link AbstractMasterAgent} the start of the simulation.
 */
public class StartSynch {

    private boolean started;

    public StartSynch() {
        started = false;
    }

    public synchronized void waitStart() {
        while (!started) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        started = false;
    }

    public synchronized void notifyStarted() {
        started = true;
        notifyAll();
    }

    public synchronized void reset() {
        started = false;
    }
}
