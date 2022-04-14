package pcd02.application;

import pcd02.controller.*;
import pcd02.model.Model;
import pcd02.model.ModelImpl;
import pcd02.view.SimulationView;
import pcd02.view.View;

public class Main {

    public static void main(String[] args) {
        int numberOfBodies = 500;
        Model model = new ModelImpl(numberOfBodies);
        View view = new SimulationView();
        Controller controller = new ControllerImpl(model, view);
        view.start();
        controller.execute();
    }
}
