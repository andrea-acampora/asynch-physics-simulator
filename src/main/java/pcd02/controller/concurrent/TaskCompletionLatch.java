package pcd02.controller.concurrent;

/**
 * Implemented as a Monitor, used by {@link AbstractMasterAgent} to wait for the end of worker's tasks execution.
 */
public class TaskCompletionLatch {

    private final int nTasks;
    private int nCompletionsNotified;

    TaskCompletionLatch(int nTasks) {
        this.nTasks = nTasks;
        this.nCompletionsNotified = 0;
    }

    public synchronized void reset() {
        this.nCompletionsNotified = 0;
    }

    public synchronized void waitCompletion() throws InterruptedException {
        while (nCompletionsNotified < nTasks) {
            wait();
        }
    }

    public synchronized void notifyCompletion() {
        this.nCompletionsNotified++;
        notifyAll();
    }

}
