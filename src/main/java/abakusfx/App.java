package abakusfx;

import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import abakus.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

	private static final Logger log = LogManager.getLogger();

	@Override
	public void start(final Stage primaryStage) throws Exception {
		Locale.setDefault(Constants.locale);

		final InputStream stream = getClass().getClassLoader().getResourceAsStream("logo.png");
		if (stream == null)
			log.warn("Could not load application icon");
		else
			primaryStage.getIcons().add(new Image(stream));

		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("app.fxml"));
		final Parent root = fxmlLoader.load();
		final AppController mainController = fxmlLoader.getController();

		primaryStage.setTitle("Abakus");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		mainController.fill(new AppTitle(primaryStage));
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

class AppTitle {
	private static final Logger log = LogManager.getLogger();

	private final Stage stage;

	private String project;
	private boolean isDirty;

	AppTitle(final Stage stage) {
		this.stage = stage;
		this.project = null;
		this.isDirty = false;
	}

	void updateProject(final String newProject) {
		log.debug("Updating title: project = '{}'", newProject);
		this.project = newProject;
		updateTitle();
	}

	void updateIsDirty(final boolean newIsDirty) {
		log.debug("Updating title: isDirty = '{}'", newIsDirty);
		this.isDirty = newIsDirty;
		updateTitle();
	}

	private void updateTitle() {
		String pName = project;
		if (pName != null && pName.endsWith(".aba"))
			pName = pName.substring(0, pName.length() - 4);
		final String projectPart = pName != null ? String.format(": %s", pName) : "";
		final String dirtyPart = isDirty ? "*" : "";
		stage.setTitle(String.format("Abakus%s%s", projectPart, dirtyPart));
	}
}