package pcd02.model.concurrent;

import pcd02.controller.concurrent.AbstractMasterAgent;
import pcd02.controller.concurrent.WorkerAgent;

import java.util.LinkedList;

/**
 * Implemented as a monitor, it is used by {@link AbstractMasterAgent} to assign the tasks at {@link WorkerAgent}.
 */
public class TaskBag {

    private final LinkedList<Task> buffer;

    public TaskBag() {
        this.buffer = new LinkedList<>();
    }

    public synchronized void addNewTask(Task task) {
        this.buffer.addLast(task);
        notifyAll();
    }

    public synchronized Task getATask() throws InterruptedException {
        while(buffer.isEmpty()){
            wait();
        }
        return buffer.removeFirst();
    }
}
