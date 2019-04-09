package net.luxvacuos.lightengine.demo.ui;

import static net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment.BOTTOM;
import static net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment.CENTER;
import static net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment.RIGHT;
import static net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment.STRETCH;
import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.LANG;
import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import java.util.Arrays;

import net.luxvacuos.hybrid.states.States;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.FlowLayout;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.FlowLayout.Direction;
import net.luxvacuos.lightengine.client.ui.v2.Button;
import net.luxvacuos.lightengine.client.ui.v2.Dropdown;
import net.luxvacuos.lightengine.client.ui.v2.Editbox;
import net.luxvacuos.lightengine.client.ui.v2.Text;
import net.luxvacuos.lightengine.client.ui.v2.surfaces.BackgroundSurface;
import net.luxvacuos.lightengine.demo.Global;
import net.luxvacuos.lightengine.universal.core.Task;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateChangeTask;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class MainSurface extends Surface {

	private Surface mainMenu, multiplayerMenu;

	private String level;

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setHorizontalAlignment(STRETCH).setVerticalAlignment(STRETCH);
		super.setBackgroundColor("#FFFFFFFF");
		super.addSurface(new BackgroundSurface().setHorizontalAlignment(STRETCH).setVerticalAlignment(STRETCH));
		Text version = new Text(" Light Engine " + REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/version")));
		version.setForegroundColor("#000000FF");
		version.setHorizontalAlignment(RIGHT).setVerticalAlignment(BOTTOM);
		version.setBackgroundColor("#FFFFFFC8");
		super.addSurface(version);
		Text versionUni = new Text(
				" Universal " + REGISTRY.getRegistryItem(KeyCache.getKey("/Light Engine/universalVersion")));
		versionUni.setForegroundColor("#000000FF");
		versionUni.setHorizontalAlignment(RIGHT).setVerticalAlignment(BOTTOM);
		versionUni.setBackgroundColor("#FFFFFFC8");
		versionUni.setY(-24);
		super.addSurface(versionUni);
		setupMainMenu();
	}

	private void setupMainMenu() {
		mainMenu = new Surface();
		mainMenu.setBorder(1).setBorderColor("#000000FF");
		mainMenu.setBackgroundColor("#666666FF");
		super.addSurface(mainMenu);
		mainMenu.setLayout(new FlowLayout().setDirection(Direction.VERTICAL));
		mainMenu.setVerticalAlignment(CENTER).setHorizontalAlignment(CENTER);

		Button playButton = new Button(LANG.getRegistryItem("lightengine.mainwindow.btnplay"));
		Button optionsButton = new Button(LANG.getRegistryItem("lightengine.mainwindow.btnoptions"));
		Button exitButton = new Button(LANG.getRegistryItem("lightengine.mainwindow.btnexit"));

		playButton.setMargin(5, 0).setHorizontalAlignment(CENTER).setWidth(200).setHeight(20);
		optionsButton.setMargin(5, 0).setHorizontalAlignment(CENTER).setWidth(200).setHeight(20);
		exitButton.setMargin(5, 0).setHorizontalAlignment(CENTER).setWidth(200).setHeight(20);

		playButton.setButtonEvent(() -> {
			mainMenu.removeSurfaceFromRoot();
			setupMultiplayerMenu();
		});
		optionsButton.setButtonEvent(() -> {
			GraphicalSubsystem.getSurfaceManager().addSurface(new OptionsSurface());
		});

		exitButton.setButtonEvent(() -> {
			TaskManager.tm.addTaskMainThread(() -> StateMachine.dispose());
		});

		mainMenu.addSurface(playButton);
		mainMenu.addSurface(optionsButton);
		mainMenu.addSurface(exitButton);
	}

	private void setupMultiplayerMenu() {
		multiplayerMenu = new Surface();
		multiplayerMenu.setBorder(1).setBorderColor("#000000FF");
		multiplayerMenu.setWidth(300).setHeight(250);
		multiplayerMenu.setBackgroundColor("#666666FF");
		super.addSurface(multiplayerMenu);
		multiplayerMenu.setVerticalAlignment(CENTER).setHorizontalAlignment(CENTER);

		Editbox address = new Editbox("Address");
		address.setHorizontalAlignment(CENTER);
		address.setY(105);

		Button playButton = new Button(LANG.getRegistryItem("lightengine.mpwindow.btnplay"));
		playButton.setHorizontalAlignment(CENTER).setVerticalAlignment(BOTTOM);
		playButton.setY(-20);
		playButton.setButtonEvent(() -> {
			if (level == null)
				return;
			Task<Boolean> t = TaskManager.tm.submitBackgroundThread(new StateChangeTask(level));
			if (!t.get())
				return;
			Global.ip = address.getInputText();
			super.removeSurfaceFromRoot();
		});

		Button goBack = new Button("Back");
		goBack.setButtonEvent(() -> {
			multiplayerMenu.removeSurfaceFromRoot();
			setupMainMenu();
		});

		Text text = new Text(LANG.getRegistryItem("lightengine.mpwindow.txtlvl"));
		text.setHorizontalAlignment(CENTER);
		text.setY(15);
		Text textAdd = new Text(LANG.getRegistryItem("lightengine.mpwindow.txtadd"));
		textAdd.setHorizontalAlignment(CENTER);
		textAdd.setY(80);

		Dropdown<String> levels = new Dropdown<>("Select Level");
		levels.setElements(Arrays.asList("Level0", "Level1", "Level2", "Level3", "Level4", States.CITY_STATE));
		levels.setHorizontalAlignment(CENTER);
		levels.setY(40);

		levels.setDropdownEvent((e) -> {
			level = e;
		});

		multiplayerMenu.addSurface(playButton);
		multiplayerMenu.addSurface(goBack);
		multiplayerMenu.addSurface(text);
		multiplayerMenu.addSurface(textAdd);
		multiplayerMenu.addSurface(levels);
		multiplayerMenu.addSurface(address);
	}

}
