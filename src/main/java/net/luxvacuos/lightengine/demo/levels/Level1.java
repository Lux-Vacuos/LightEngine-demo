package net.luxvacuos.lightengine.demo.levels;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import io.netty.channel.ChannelHandlerContext;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.NetworkSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.network.ClientNetworkHandler;
import net.luxvacuos.lightengine.client.network.LocalNetworkHandler;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow.WindowClose;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.rendering.opengl.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.WaterTile;
import net.luxvacuos.lightengine.demo.Global;
import net.luxvacuos.lightengine.demo.ecs.entities.FreeCamera;
import net.luxvacuos.lightengine.demo.ui.LoadWindow;
import net.luxvacuos.lightengine.demo.ui.PauseWindow;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.network.ManagerChannelHandler;
import net.luxvacuos.lightengine.universal.network.SharedChannelHandler;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Disconnect;

public class Level1 extends AbstractState {

	private PauseWindow pauseWindow;
	private LoadWindow loadWindow;

	private LocalNetworkHandler nh;

	private List<WaterTile> waterTiles;

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

		// waterTiles = new ArrayList<>();
		// for (int x = -32; x <= 700; x++)
		// for (int z = -32; z <= 700; z++)
		// waterTiles.add(new WaterTile(x * WaterTile.TILE_SIZE, 0.5f, z *
		// WaterTile.TILE_SIZE));

		RenderEntity city = new RenderEntity("", "levels/level1/city.fbx");

		nh.getEngine().addEntity(city);
		loadWindow.completedStart();
		super.start();
	}

	@Override
	public void end() {
		Global.loaded = false;
		// TaskManager.addTask(() -> waterTiles.clear());
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
