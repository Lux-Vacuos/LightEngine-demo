/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.demo.ui;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.LANG;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.rendering.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.ui.Alignment;
import net.luxvacuos.lightengine.client.ui.Button;
import net.luxvacuos.lightengine.client.ui.ComponentWindow;
import net.luxvacuos.lightengine.client.ui.ModalWindow;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;

public class MainWindow extends ComponentWindow {

	public MainWindow(int x, int y, int w, int h) {
		super(x, y, w, h, LANG.getRegistryItem("lightengine.mainwindow.name"));
	}

	@Override
	public void initApp() {
		super.setBackgroundColor(0.4f, 0.4f, 0.4f, 1f);

		Button playButton = new Button(0, 120, 200, 40, LANG.getRegistryItem("lightengine.mainwindow.btnplay"));
		Button optionsButton = new Button(0, -70, 200, 40, LANG.getRegistryItem("lightengine.mainwindow.btnoptions"));
		Button exitButton = new Button(0, -120, 200, 40, LANG.getRegistryItem("lightengine.mainwindow.btnexit"));

		playButton.setPreicon(Theme.ICON_BLACK_RIGHT_POINTING_TRIANGLE);
		optionsButton.setPreicon(Theme.ICON_GEAR);
		exitButton.setPreicon(Theme.ICON_LOGIN);

		playButton.setAlignment(Alignment.CENTER);
		playButton.setWindowAlignment(Alignment.CENTER);
		optionsButton.setAlignment(Alignment.CENTER);
		optionsButton.setWindowAlignment(Alignment.CENTER);
		exitButton.setAlignment(Alignment.CENTER);
		exitButton.setWindowAlignment(Alignment.CENTER);

		playButton.setOnButtonPress(() -> {
			GraphicalSubsystem.getWindowManager()
					.addWindow(new MultiplayerWindow(w / 2 - 250 + x, y - 100, 500, 400));
		});

		optionsButton.setOnButtonPress(() -> {
			GraphicalSubsystem.getWindowManager().addWindow(new OptionsWindow());
		});

		exitButton.setOnButtonPress(() -> {
			super.closeWindow();
		});

		super.addComponent(playButton);
		super.addComponent(optionsButton);
		super.addComponent(exitButton);

		super.setWindowClose(WindowClose.DO_NOTHING);
		super.initApp();
	}

	@Override
	public void processWindowMessage(int message, Object param) {
		switch (message) {
		case WindowMessage.WM_CLOSE:
			WindowClose wc = (WindowClose) param;
			switch (wc) {
			case DISPOSE:
				break;
			case DO_NOTHING:
				ModalWindow window = new ModalWindow(340, 200, "", "Exit Demo");
				GraphicalSubsystem.getWindowManager().addWindow(window);
				TaskManager.addTask(() -> {
					window.setOnAccept(() -> {
						new Thread(() -> {
							while (GraphicalSubsystem.getWindowManager().getTotalWindows() > 0)
								try {
									Thread.sleep(400);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							TaskManager.addTask(() -> StateMachine.stop());
						}).start();
						super.setWindowClose(WindowClose.DISPOSE);
						GraphicalSubsystem.getWindowManager().closeAllWindows();
					});
				});
				break;
			}
			break;
		}
		super.processWindowMessage(message, param);
	}

}
