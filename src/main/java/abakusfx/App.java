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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import abakus.Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

	private static final Logger log = LogManager.getLogger();

	private AppResourceLoader resourceLoader = new AppResourceLoader();

	@Override
	public void start(final Stage primaryStage) throws IOException {
		Locale.setDefault(Constants.locale);

		String props = resourceLoader.loadVersionProperties();
		for (String prop : props.split("\n"))
			log.info(prop);

		final InputStream stream = resourceLoader.getResourceStream("logo.png");
		if (stream == null)
			log.warn("Could not load application icon");
		else
			primaryStage.getIcons().add(new Image(stream));

		final FXMLLoader fxmlLoader = resourceLoader.getFxmlLoader("app.fxml");
		final Parent root = fxmlLoader.load();
		final AppController appController = fxmlLoader.getController();

		primaryStage.setTitle("Abakus");
		primaryStage.setScene(new Scene(root));
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
