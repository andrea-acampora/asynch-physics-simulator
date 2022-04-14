package pcd02.controller.concurrent;

/**
 * Implemented as a Monitor, used to stop the current simulation.
 */
public class StopFlag {

    private boolean flag;

    public StopFlag() {
        this.flag = false;
    }

    public synchronized void reset() {
        this.flag = false;
    }

    public synchronized void set() {
        this.flag = true;
    }

    public synchronized boolean isSet() {
        return this.flag;
    }
}
