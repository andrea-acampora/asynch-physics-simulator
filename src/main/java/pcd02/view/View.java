package pcd02.view;

import pcd02.controller.InputListener;
import pcd02.model.SimulationState;

/**
 * The view of the application.
 */
public interface View {

    /**
     *
     * @param state The information of the simulation to display.
     */
    void display(SimulationState state);

    /**
     * Set visible the GUI.
     */
    void start();

    /**
     *
     * @param listener add a listener that will be notified of the press of GUI buttons.
     */
    void addListener(InputListener listener);
}
