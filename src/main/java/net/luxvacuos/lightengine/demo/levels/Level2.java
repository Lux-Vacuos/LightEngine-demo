package net.luxvacuos.lightengine.demo.levels;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.lwjgl.glfw.GLFW;

import io.netty.channel.ChannelHandlerContext;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.network.Client;
import net.luxvacuos.lightengine.client.network.ClientNetworkHandler;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow.WindowClose;
import net.luxvacuos.lightengine.client.rendering.opengl.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.opengl.Renderer;
import net.luxvacuos.lightengine.client.ui.windows.GameWindow;
import net.luxvacuos.lightengine.demo.Global;
import net.luxvacuos.lightengine.demo.ui.LoadWindow;
import net.luxvacuos.lightengine.demo.ui.PauseWindow;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.network.SharedChannelHandler;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Disconnect;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class Level2 extends AbstractState {

	private GameWindow gameWindow;
	private PauseWindow pauseWindow;
	private LoadWindow loadWindow;

	private Client client;
	private ClientNetworkHandler nh;

	public Level2() {
		super("Level2");
	}

	@Override
	public void start() {
		loadWindow = new LoadWindow();
		GraphicalSubsystem.getWindowManager().addWindow(loadWindow);
		Renderer.init(GraphicalSubsystem.getMainWindow());
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), true);
		client = new Client();
		if (!Global.ip.isEmpty())
			client.setHost(Global.ip);
		nh = new ClientNetworkHandler(client, null);
		client.run(nh, new SharedChannelHandler() {

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				if (msg instanceof Disconnect) {
					handleDisconnect((Disconnect) msg);
				}
				super.channelRead(ctx, msg);
			}

			private void handleDisconnect(Disconnect disconnect) {
				Global.exitWorld = true;
				if (pauseWindow != null)
					pauseWindow.notifyWindow(WindowMessage.WM_CLOSE, WindowClose.DO_NOTHING);
			}
		});

		Renderer.setOnResize(() -> {
			ClientComponents.PROJECTION_MATRIX.get(nh.getPlayer())
					.setProjectionMatrix(Renderer.createProjectionMatrix(
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")),
							ClientVariables.NEAR_PLANE, ClientVariables.FAR_PLANE));
		});
		RenderEntity scene = new RenderEntity("", "levels/level2/models/level.blend");
		nh.getEngine().addEntity(scene);

		client.sendPacket(new ClientConnect(Components.UUID.get(nh.getPlayer()).getUUID(),
				Components.NAME.get(nh.getPlayer()).getName()));
		gameWindow = new GameWindow(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")));
		GraphicalSubsystem.getWindowManager().addWindow(0, gameWindow);
		super.start();
	}

	@Override
	public void end() {
		Global.loaded = false;
		client.sendPacket(new ClientDisconnect(Components.UUID.get(nh.getPlayer()).getUUID(),
				Components.NAME.get(nh.getPlayer()).getName()));
		nh.dispose();
		client.end();
		super.end();
	}

	@Override
	public void update(float delta) {
		Window window = GraphicalSubsystem.getMainWindow();
		if (!Global.loaded) {
			if (window.getAssimpResourceLoader().isDoneLoading()) {
				loadWindow.closeWindow();
				Global.loaded = true;
			}
			return;
		}
		KeyboardHandler kbh = window.getKeyboardHandler();
		if (!Global.paused) {
			nh.update(delta);
			Renderer.getLightRenderer().update(delta);
			ParticleDomain.update(delta, nh.getPlayer());

			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), false);
				Global.paused = true;
				pauseWindow = new PauseWindow();
				GraphicalSubsystem.getWindowManager().addWindow(pauseWindow);
			}
		} else if (Global.exitWorld) {
			gameWindow.closeWindow();
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
		if (!Global.loaded)
			return;
		Renderer.render(nh.getEngine().getEntities(), ParticleDomain.getParticles(), null, nh.getPlayer(),
				nh.getWorldSimulation(), nh.getSun(), alpha);
	}

}
