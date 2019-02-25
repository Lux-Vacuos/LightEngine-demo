import net.luxvacuos.lightengine.client.loader.ClientLoader;
import net.luxvacuos.lightengine.demo.MainMenuState;
import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;

public class EngineLoader implements IEngineLoader {

	@Override
	public void loadExternal() {
		TaskManager.tm.addTaskMainThread(() -> StateMachine.registerState(new MainMenuState()));
	}

	public static void main(String[] args) {
		new ClientLoader(new EngineLoader(), "-p", "LightEngineDemo");
	}

}
