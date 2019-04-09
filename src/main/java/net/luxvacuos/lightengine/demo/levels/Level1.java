package net.luxvacuos.lightengine.demo.levels;

import java.util.Random;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.client.ecs.entities.WaterTileEnt;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.network.LocalNetworkHandler;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.world.ParticleDomain;
import net.luxvacuos.lightengine.demo.Global;
import net.luxvacuos.lightengine.demo.ecs.entities.FreeCamera;
import net.luxvacuos.lightengine.demo.ui.LoadWindow;
import net.luxvacuos.lightengine.demo.ui.PauseWindow;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;

public class Level1 extends AbstractState {

	private PauseWindow pauseWindow;
	private LoadWindow loadWindow;

	private LocalNetworkHandler nh;

	public Level1() {
		super("Level1");
	}

	@Override
	public void start() {
		loadWindow = new LoadWindow();
		GraphicalSubsystem.getWindowManager().addWindow(loadWindow);
		GraphicalSubsystem.getRenderer().init();
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), true);

		nh = new LocalNetworkHandler(
				new FreeCamera("player" + new Random().nextInt(1000), UUID.randomUUID().toString()));

		for (int x = -32; x <= 700; x++)
			for (int z = -32; z <= 700; z++)
				nh.getEngine().addEntity(new WaterTileEnt(x * WaterTileEnt.TILE_SIZE, 1f, z * WaterTileEnt.TILE_SIZE));

		RenderEntity city = new RenderEntity("", "levels/level1/city.fbx");

		nh.getEngine().addEntity(city);
		loadWindow.completedStart();
		super.start();
	}

	@Override
	public void end() {
		Global.loaded = false;
		GraphicalSubsystem.getRenderer().dispose();
		nh.dispose();
		super.end();
	}

	@Override
	public void update(float delta) {
		Window window = GraphicalSubsystem.getMainWindow();
		if (!Global.loaded) {
			if (window.getAssimpResourceLoader().isDoneLoading()) {
				Global.loaded = true;
			}
			return;
		}
		KeyboardHandler kbh = window.getKeyboardHandler();
		if (!Global.paused) {
			nh.update(delta);
			ParticleDomain.update(delta, nh.getCamera());

			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), false);
				Global.paused = true;
				pauseWindow = new PauseWindow();
				GraphicalSubsystem.getWindowManager().addWindow(pauseWindow);
			}
		} else if (Global.exitWorld) {
			Global.exitWorld = false;
			Global.paused = false;
			StateMachine.setCurrentState(StateNames.MAIN);
		} else {
			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				pauseWindow.closeWindow();
			}
		}

	}

	@Override
	public void render(float alpha) {
		GraphicalSubsystem.getRenderer().render(nh, alpha);
	}

}
