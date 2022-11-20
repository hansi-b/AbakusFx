/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2022 Hans Bering
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
 */
package abakusfx;

import java.util.Locale;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import abakus.Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

	private static final Logger log = LogManager.getLogger();

	private final AppResourceLoader appResourceLoader = new AppResourceLoader();

	@Override
	public void start(final Stage primaryStage) {
		Locale.setDefault(Constants.locale);

		String props = appResourceLoader.loadVersionProperties();
		for (String prop : props.split("\n"))
			log.info(prop);

		Image logo = appResourceLoader.loadLogo();
		if (logo == null)
			log.warn("Could not load application icon");
		else
			primaryStage.getIcons().add(logo);

		AppController appController = appResourceLoader.loadApp(primaryStage);

		primaryStage.setTitle("Abakus");
		primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
			if (!appController.showUnsavedChangesDialogue())
				e.consume();
			else
				Platform.exit();
		});

		primaryStage.show();

		appController.addTitleListeners(primaryStage::setTitle);
		appController.fill(getParameters());
	}

	public static void main(final String[] args) {
		final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("org.javamoney.moneta");
		logger.setLevel(Level.WARNING);

		launch(args);
	}

	@Override
	public void stop() {
		log.info("Stopping");
	}
}
