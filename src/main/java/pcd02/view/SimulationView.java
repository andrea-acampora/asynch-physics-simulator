package pcd02.view;

import pcd02.controller.InputListener;
import pcd02.model.SimulationState;


public class SimulationView implements View {

    private final SimulationGUI gui;
    private static final double FPS = 120;
    private long displayTime;
    private int counter = 0;


    public SimulationView() {
        this.gui = new SimulationGUI();
    }

    @Override
    public void display(SimulationState state) {
        long currentDisplayTime = System.currentTimeMillis();
        double elapsed = currentDisplayTime - this.displayTime;
        if (elapsed > (( 1 / FPS) * 1000)) {
            this.gui.display(state);
            this.displayTime = currentDisplayTime;
        }
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
