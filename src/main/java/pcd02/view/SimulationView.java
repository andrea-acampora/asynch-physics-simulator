package pcd02.view;

import pcd02.controller.InputListener;
import pcd02.model.SimulationState;


public class SimulationView implements View {

    private final SimulationGUI gui;

    public SimulationView() {
        this.gui = new SimulationGUI();
    }

    @Override
    public void display(SimulationState state) {
        this.gui.display(state);
    }

    @Override
    public void start() {
        this.gui.start();
    }

    @Override
    public void addListener(InputListener listener) {
        this.gui.addListener(listener);
    }
}
