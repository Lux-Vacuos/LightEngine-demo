package net.luxvacuos.lightengine.demo.ui;

import static net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment.CENTER;
import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.LANG;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.FlowLayout;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.layouts.FlowLayout.Direction;
import net.luxvacuos.lightengine.client.ui.v2.Button;
import net.luxvacuos.lightengine.client.ui.v2.surfaces.RendererSurface;
import net.luxvacuos.lightengine.demo.Global;

public class GameSurface extends Surface {

	private RendererSurface rndSurface;

	private Surface pauseMenu;

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setHorizontalAlignment(Alignment.STRETCH).setVerticalAlignment(Alignment.STRETCH);
		rndSurface = new RendererSurface();
		rndSurface.setHorizontalAlignment(Alignment.STRETCH).setVerticalAlignment(Alignment.STRETCH);
		GraphicalSubsystem.getRenderer().setSurface(rndSurface);
		super.addSurface(rndSurface);
	}

	public void setupPauseMenu() {
		pauseMenu = new Surface();
		pauseMenu.setBorder(1).setBorderColor("#000000FF");
		pauseMenu.setBackgroundColor("#666666FF");
		super.addSurface(pauseMenu);
		pauseMenu.setLayout(new FlowLayout().setDirection(Direction.VERTICAL));
		pauseMenu.setVerticalAlignment(CENTER).setHorizontalAlignment(CENTER);

		Button optionsButton = new Button(LANG.getRegistryItem("lightengine.mainwindow.btnoptions"));
		Button exitButton = new Button("Back to Main Menu");

		optionsButton.setMargin(5, 5).setHorizontalAlignment(CENTER).setWidth(200).setHeight(20);
		exitButton.setMargin(5, 5).setHorizontalAlignment(CENTER).setWidth(200).setHeight(20);

		optionsButton.setButtonEvent(() -> {
		});

		exitButton.setButtonEvent(() -> {
			Global.exitWorld = true;
		});

		pauseMenu.addSurface(optionsButton);
		pauseMenu.addSurface(exitButton);
	}

	public void removePauseMenu() {
		super.removeSurface(pauseMenu);
	}

	public RendererSurface getRndSurface() {
		return rndSurface;
	}

}
