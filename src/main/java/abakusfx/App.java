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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

		appController.fill(new AppTitle(primaryStage));
		Parameters parameters = getParameters();

		List<String> unnamed = parameters.getUnnamed();
		if (!unnamed.isEmpty()) {
			log.debug("got unnamed parameters: {}", unnamed);
			appController.loadAndShow(new File(unnamed.get(0)));
		}
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