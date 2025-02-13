package net.luxvacuos.lightengine.demo.levels;

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
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow.WindowClose;
import net.luxvacuos.lightengine.client.world.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
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

public class Level3 extends AbstractState {

	private PauseWindow pauseWindow;
	private LoadWindow loadWindow;

	private ClientNetworkHandler nh;
	private SharedChannelHandler local;

	public Level3() {
		super("Level3");
	}

	@Override
	public void start() {
		loadWindow = new LoadWindow();
		GraphicalSubsystem.getWindowManager().addWindow(loadWindow);
		GraphicalSubsystem.getRenderer().init();
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), true);

		local = new SharedChannelHandler() {

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
		};

		ManagerChannelHandler mch = NetworkSubsystem.getManagerChannelHandler();

		nh = new ClientNetworkHandler(
				new FreeCamera("player" + new Random().nextInt(1000), UUID.randomUUID().toString()));
		mch.addChannelHandler(nh);
		mch.addChannelHandler(local);

		try {
			NetworkSubsystem.connect(Global.ip, 44454);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		NetworkSubsystem.sendPacket(new ClientConnect(Components.UUID.get(nh.getPlayer()).getUUID(),
				Components.NAME.get(nh.getPlayer()).getName()));

		RenderEntity scene = new RenderEntity("", "levels/level3/models/building.fbx");
		nh.getEngine().addEntity(scene);
		loadWindow.completedStart();
		super.start();
	}

	@Override
	public void end() {
		Global.loaded = false;
		NetworkSubsystem.sendPacket(new ClientDisconnect(Components.UUID.get(nh.getPlayer()).getUUID(),
				Components.NAME.get(nh.getPlayer()).getName()));
		try {
			NetworkSubsystem.disconnect();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ManagerChannelHandler mch = NetworkSubsystem.getManagerChannelHandler();
		mch.removeChannelHandler(nh);
		mch.removeChannelHandler(local);
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
