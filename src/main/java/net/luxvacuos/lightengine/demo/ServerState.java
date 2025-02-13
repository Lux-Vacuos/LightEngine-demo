package net.luxvacuos.lightengine.demo;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.Transform;

import net.luxvacuos.lightengine.server.commands.SayCommand;
import net.luxvacuos.lightengine.server.commands.ServerCommandManager;
import net.luxvacuos.lightengine.server.commands.StopCommand;
import net.luxvacuos.lightengine.server.console.Console;
import net.luxvacuos.lightengine.server.core.subsystems.NetworkSubsystem;
import net.luxvacuos.lightengine.server.loader.ServerLoader;
import net.luxvacuos.lightengine.server.network.ServerNetworkHandler;
import net.luxvacuos.lightengine.universal.commands.ICommandManager;
import net.luxvacuos.lightengine.universal.commands.StateCommand;
import net.luxvacuos.lightengine.universal.commands.TimeCommand;
import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.network.ManagerChannelHandler;
import net.luxvacuos.lightengine.universal.util.VectoVec;
import net.luxvacuos.lightengine.universal.util.registry.Key;
import net.luxvacuos.lightengine.universal.world.DynamicObject;
import net.luxvacuos.lightengine.universal.world.PhysicsSystem;

public class ServerState extends AbstractState {
	private Console console;
	private ICommandManager commandManager;
	private ServerNetworkHandler nh;

	public ServerState() {
		super(StateNames.MAIN);
	}

	@Override
	public void init() {
		nh = new ServerNetworkHandler();

		ManagerChannelHandler mch = NetworkSubsystem.getManagerChannelHandler();
		mch.addChannelHandler(nh);

		try {
			NetworkSubsystem.bind((int) REGISTRY.getRegistryItem(new Key("/Light Engine/Server/port")));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		CollisionShape groundShape = new BoxShape(VectoVec.toVec3(new Vector3f(30, 2, 30)));
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(VectoVec.toVec3(new Vector3f(0, -2, 0)));
		nh.getEngine().getSystem(PhysicsSystem.class).addCollision(new DynamicObject(groundShape, groundTransform, 0));
		CollisionShape wallShape = new BoxShape(VectoVec.toVec3(new Vector3f(30, 3, 1)));

		groundTransform.setIdentity();
		groundTransform.origin.set(VectoVec.toVec3(new Vector3f(0, 0, -31)));
		nh.getEngine().getSystem(PhysicsSystem.class).addCollision(new DynamicObject(wallShape, groundTransform, 0));
		groundTransform.setIdentity();
		groundTransform.origin.set(VectoVec.toVec3(new Vector3f(0, 0, 31)));
		nh.getEngine().getSystem(PhysicsSystem.class).addCollision(new DynamicObject(wallShape, groundTransform, 0));

		groundTransform.setIdentity();
		groundTransform.origin.set(VectoVec.toVec3(new Vector3f(-31, 0, 0)));
		Quaternionf q = new Quaternionf();
		q.identity();
		q.fromAxisAngleDeg(new Vector3f(0, 1, 0), (float) Math.toRadians(90));
		groundTransform.setRotation(VectoVec.toQuat4(q));
		nh.getEngine().getSystem(PhysicsSystem.class).addCollision(new DynamicObject(wallShape, groundTransform, 0));
		groundTransform.setIdentity();
		groundTransform.origin.set(VectoVec.toVec3(new Vector3f(31, 0, 0)));
		groundTransform.setRotation(VectoVec.toQuat4(q));
		nh.getEngine().getSystem(PhysicsSystem.class).addCollision(new DynamicObject(wallShape, groundTransform, 0));

		commandManager = new ServerCommandManager(System.out);
		commandManager.registerCommand(new StopCommand());
		commandManager.registerCommand(new SayCommand());
		commandManager.registerCommand(new TimeCommand(nh.getWorldSimulation()));
		commandManager.registerCommand(new StateCommand());

		console = new Console();
		console.setCommandManager(commandManager);
		console.start();

	}

	@Override
	public void dispose() {
		nh.dispose();
		console.stop();
		try {
			NetworkSubsystem.disconnect();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(float delta) {
		nh.update(delta);
	}

	public static void main(String[] args) {
		new ServerLoader(new IEngineLoader() {
			@Override
			public void loadExternal() {
				TaskManager.tm.addTaskMainThread(() -> StateMachine.registerState(new ServerState()));
			}
		}, "-p", "LightEngineDemoServer");
	}

}
