package net.luxvacuos.lightengine.demo.ui;

import static net.luxvacuos.lightengine.client.rendering.nanovg.v2.Alignment.*;
import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.LANG;

import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.v2.Surface;
import net.luxvacuos.lightengine.client.ui.v2.Button;
import net.luxvacuos.lightengine.client.ui.v2.ScrollbarVertical;

public class OptionsSurface extends Surface {

	private Surface mainMenu, graphicsMenu;

	@Override
	public void init(long ctx, MouseHandler mh, KeyboardHandler kh) {
		super.init(ctx, mh, kh);
		super.setHorizontalAlignment(CENTER).setVerticalAlignment(CENTER);
		super.setBorder(1).setBorderColor("#000000FF");
		super.setBackgroundColor("#666666FF");
		super.setWidth(500).setHeight(300);
		setupMainMenu();
	}

	private void setupMainMenu() {
		mainMenu = new Surface();
		mainMenu.setHorizontalAlignment(STRETCH).setVerticalAlignment(STRETCH);

		Button goBack = new Button("Back");
		goBack.setButtonEvent(() -> {
			super.removeSurfaceFromRoot();
		});

		Button graphics = new Button(LANG.getRegistryItem("lightengine.optionswindow.btngraphics"));
		graphics.setButtonEvent(() -> {
			mainMenu.removeSurfaceFromRoot();
			setupGraphicsMenu();
		});
		graphics.setY(40);

		mainMenu.addSurface(goBack);
		mainMenu.addSurface(graphics);
		super.addSurface(mainMenu);
	}

	private void setupGraphicsMenu() {
		graphicsMenu = new Surface();
		graphicsMenu.setHorizontalAlignment(STRETCH).setVerticalAlignment(STRETCH);

		Button goBack = new Button("Back");
		goBack.setButtonEvent(() -> {
			graphicsMenu.removeSurfaceFromRoot();
			setupMainMenu();
		});

		graphicsMenu.addSurface(new ScrollbarVertical().setHorizontalAlignment(RIGHT));

		graphicsMenu.addSurface(goBack);

		super.addSurface(graphicsMenu);
	}

}
