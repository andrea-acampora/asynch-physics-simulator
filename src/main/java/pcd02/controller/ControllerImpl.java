package pcd02.controller;

import pcd02.controller.concurrent.StopFlag;
import pcd02.controller.concurrent.StartSynch;
import pcd02.model.Model;
import pcd02.view.View;

public class ControllerImpl implements InputListener, Controller {

	private final Model model;
	private final View view;
	private final StopFlag stopFlag;
	private final StartSynch startSynch;
	SimulationService simulationService;

	private static final int NUMBER_OF_STEPS = 50000;

	public ControllerImpl(final Model model, final View view) {
		this.model = model;
		this.view = view;
		this.view.addListener(this);
		this.stopFlag = new StopFlag();
		this.startSynch = new StartSynch();
		this.simulationService = new SimulationService(model.getState(), NUMBER_OF_STEPS, view, startSynch, stopFlag, model.getTaskFactory());
	}

	public void start() {
		stopFlag.reset();
		startSynch.notifyStarted();
	}

	public void stop() {
		startSynch.reset();
		stopFlag.set();
	}

	@Override
	public void execute() {
		simulationService.start();
	}
}
