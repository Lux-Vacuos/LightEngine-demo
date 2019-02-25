package net.luxvacuos.lightengine.demo.levels;

import java.util.Random;
import java.util.UUID;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.FPSPlayer;
import net.luxvacuos.lightengine.client.ecs.entities.PlayerCamera;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.network.LocalNetworkHandler;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.opengl.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.ParticleTexture;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.resources.ResourcesManager;
import net.luxvacuos.lightengine.client.world.particles.ParticleSystem;
import net.luxvacuos.lightengine.demo.Global;
import net.luxvacuos.lightengine.demo.ui.LoadWindow;
import net.luxvacuos.lightengine.demo.ui.PauseWindow;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;

public class Level2 extends AbstractState {

	private PauseWindow pauseWindow;
	private LoadWindow loadWindow;

	private LocalNetworkHandler nh;

	private ParticleSystem particleSystem;
	private Texture fire;

	public Level2() {
		super("Level2");
	}

	@Override
	public void start() {
		loadWindow = new LoadWindow();
		GraphicalSubsystem.getWindowManager().addWindow(loadWindow);
		GraphicalSubsystem.getRenderer().init();
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), true);

		nh = new LocalNetworkHandler(new FPSPlayer("player" + new Random().nextInt(1000), new Vector3f(3, 2, 0)));

		RenderEntity scene = new RenderEntity("", "levels/level2/models/level.blend");
		nh.getEngine().addEntity(scene);
		
		RenderEntity chara = new RenderEntity("", "levels/level2/models/character.blend");
		nh.getEngine().addEntity(chara);

		nh.setCamera(new PlayerCamera("camera", UUID.randomUUID().toString()));
		nh.getPlayer().addEntity(nh.getCamera());

		Light light1 = new Light(new Vector3f(0, 10, 10), new Vector3f(100), new Vector3f(60, 0, 0), 20, 18);
		light1.setShadow(true);
		//nh.getEngine().addEntity(light1);

		fire = ResourcesManager.loadTexture("textures/particles/fire0.png", null).get();

		particleSystem = new ParticleSystem(new ParticleTexture(fire.getID(), 4), 1000, 1, -1f, 3f, 6f);
		particleSystem.setDirection(new Vector3f(0, -1, 0), 0.4f);
		loadWindow.completedStart();
		super.start();
	}

	@Override
	public void end() {
		Global.loaded = false;
		ResourcesManager.disposeTexture(fire);
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
			particleSystem.generateParticles(new Vector3f(0, 1.7f, -5), delta);
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
