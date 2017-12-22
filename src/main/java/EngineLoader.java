import net.luxvacuos.lightengine.client.bootstrap.Bootstrap;
import net.luxvacuos.lightengine.demo.MainMenuState;
import net.luxvacuos.lightengine.demo.levels.Level0;
import net.luxvacuos.lightengine.demo.levels.Level1;
import net.luxvacuos.lightengine.demo.levels.Level2;
import net.luxvacuos.lightengine.demo.levels.Level3;
import net.luxvacuos.lightengine.demo.levels.Level4;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;

public class EngineLoader implements IEngineLoader {

	@Override
	public void loadExternal() {
		TaskManager.addTask(() -> StateMachine.registerState(new MainMenuState()));
		TaskManager.addTaskUpdate(() -> StateMachine.registerState(new Level0()));
		TaskManager.addTaskUpdate(() -> StateMachine.registerState(new Level1()));
		TaskManager.addTaskUpdate(() -> StateMachine.registerState(new Level2()));
		TaskManager.addTaskUpdate(() -> StateMachine.registerState(new Level3()));
		TaskManager.addTaskUpdate(() -> StateMachine.registerState(new Level4()));
	}
	
	public static void main(String[] args) {
		GlobalVariables.PROJECT = "LightEngineDemo";
		new EngineLoader().loadExternal();
		new Bootstrap(args);
	}

}
