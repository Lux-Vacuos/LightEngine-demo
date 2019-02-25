package net.luxvacuos.lightengine.demo.ui;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.Spinner;
import net.luxvacuos.lightengine.universal.core.TaskManager;

public class LoadWindow extends ComponentWindow {

	private float timerOnTop;
	private boolean completedStart;

	public LoadWindow() {
		super("Loader");
	}

	@Override
	public void initApp() {
		super.setDecorations(false);
		super.setBlurBehind(false);
		super.setBackgroundColor("#FFFFFFFF");
		super.setAlwaysOnTop(true);
		Spinner load = new Spinner(-22, 2, 20);
		load.setWindowAlignment(Alignment.RIGHT_BOTTOM);
		super.addComponent(load);
		super.initApp();

	}

	@Override
	public void alwaysUpdateApp(float delta) {
		timerOnTop += delta;
		if (timerOnTop > 1) {
			GraphicalSubsystem.getWindowManager().bringToFront(this);
			timerOnTop = 0;
		}
		if(TaskManager.tm.isEmpty() && completedStart)
			super.closeWindow();
		super.alwaysUpdateApp(delta);
	}
	
	public void completedStart() {
		completedStart = true;
	}

}
