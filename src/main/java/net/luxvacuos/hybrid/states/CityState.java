package net.luxvacuos.hybrid.states;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.network.LocalNetworkHandler;
import net.luxvacuos.lightengine.demo.ecs.entities.FreeCamera;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;

public class CityState extends AbstractState {
	
	private LocalNetworkHandler nh;

	public CityState() {
		super(States.CITY_STATE);
	}
	
	@Override
	public void start() {
		super.start();
		GraphicalSubsystem.getRenderer().init();
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), true);
		
		nh = new LocalNetworkHandler(new FreeCamera("camera"));
	}
	
	@Override
	public void end() {
		super.end();
		GraphicalSubsystem.getRenderer().dispose();
		nh.dispose();
	}

	@Override
	public void update(float delta) {
		nh.update(delta);
	}
	
	@Override
	public void render(float delta) {
		GraphicalSubsystem.getRenderer().render(nh, delta);
	}

}
