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
		TaskManager.tm.addTask(() -> StateMachine.registerState(new MainMenuState()));
		TaskManager.tm.addTaskUpdate(() -> StateMachine.registerState(new Level0()));
		TaskManager.tm.addTaskUpdate(() -> StateMachine.registerState(new Level1()));
		TaskManager.tm.addTaskUpdate(() -> StateMachine.registerState(new Level2()));
		TaskManager.tm.addTaskUpdate(() -> StateMachine.registerState(new Level3()));
		TaskManager.tm.addTaskUpdate(() -> StateMachine.registerState(new Level4()));
	}

	public static void main(String[] args) {
		GlobalVariables.PROJECT = "LightEngineDemo";
		new Bootstrap(args, new EngineLoader());
	}

}
