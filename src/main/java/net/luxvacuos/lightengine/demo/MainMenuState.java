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

package net.luxvacuos.lightengine.demo;

import net.luxvacuos.hybrid.states.CityState;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.demo.levels.Level0;
import net.luxvacuos.lightengine.demo.levels.Level1;
import net.luxvacuos.lightengine.demo.levels.Level2;
import net.luxvacuos.lightengine.demo.levels.Level3;
import net.luxvacuos.lightengine.demo.levels.Level4;
import net.luxvacuos.lightengine.demo.ui.MainSurface;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;

public class MainMenuState extends AbstractState {


	public MainMenuState() {
		super(StateNames.MAIN);
	}

	@Override
	public void init() {
		super.init();
		TaskManager.tm.addTaskBackgroundThread(() -> StateMachine.registerState(new Level0()));
		TaskManager.tm.addTaskBackgroundThread(() -> StateMachine.registerState(new Level1()));
		TaskManager.tm.addTaskBackgroundThread(() -> StateMachine.registerState(new Level2()));
		TaskManager.tm.addTaskBackgroundThread(() -> StateMachine.registerState(new Level3()));
		TaskManager.tm.addTaskBackgroundThread(() -> StateMachine.registerState(new Level4()));
		TaskManager.tm.addTaskBackgroundThread(() -> StateMachine.registerState(new CityState()));
	}

	@Override
	public void start() {
		GraphicalSubsystem.getSurfaceManager().addSurface(new MainSurface());
		super.start();
	}

	@Override
	public void end() {
		super.end();
	}

	@Override
	public void update(float delta) {
	}

}
