package abakusfx;

import abakus.Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;

public class App extends Application {

	private static final Logger log = LogManager.getLogger();

	@Override
	public void start(final Stage primaryStage) throws IOException {
		Locale.setDefault(Constants.locale);

		String props = ResourceLoader.loader.resourceAsString("version.properties");
		for (String prop : props.split("\n"))
			log.info(prop);

		final InputStream stream = ResourceLoader.loader.getResourceStream("logo.png");
		if (stream == null)
			log.warn("Could not load application icon");
		else
			primaryStage.getIcons().add(new Image(stream));

		final FXMLLoader fxmlLoader = ResourceLoader.loader.getFxmlLoader("app.fxml");
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

		appController.addTitleListeners(new AppTitle(primaryStage));
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

