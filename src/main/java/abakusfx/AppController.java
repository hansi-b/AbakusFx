package abakusfx;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import abakus.KostenRechner;
import abakus.Tarif;
import abakus.ÖtvCsvParser;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class AppController {
	static final Logger log = LogManager.getLogger();

	@FXML
	private BorderPane topLevelPane;

	@FXML
	private MenuItem saveItem;

	@FXML
	private ProjectTabsController projectTabsController;

	@FXML
	private ÜbersichtTableController übersichtTableController;

	@FXML
	private TextField stats;

	private AppPrefs prefs;

	/**
	 * whether we would want to alert the user to unsaved changes
	 */
	private BooleanProperty isSettingsChanged;

	private StringProperty currentProjectName;
	/**
	 * whether we have a project with unsaved changes
	 */
	BooleanBinding isCurrentProjectDirty;

	@FXML
	void initialize() throws IOException {

		isSettingsChanged = new SimpleBooleanProperty(false);
		currentProjectName = new SimpleStringProperty("");

		projectTabsController.setKostenRechner(initKostenRechner());
		projectTabsController.setChangedHandler(() -> isSettingsChanged.set(true));

		isCurrentProjectDirty = currentProjectName.isNotEmpty().and(isSettingsChanged);
		saveItem.disableProperty().bind(isCurrentProjectDirty.not());

		prefs = AppPrefs.Factory.create();

		if (!prefs.wasDisclaimerAccepted()) {
			prefs.setDisclaimerAccepted(displayDislaimerAndAccept());
			if (!prefs.wasDisclaimerAccepted()) {
				Platform.exit();
				log.info("Disclaimer was rejected");
			}
		}

		projectTabsController.setUpdateHandler(übersichtTableController::updateItems);
		Platform.runLater(() -> projectTabsController.focusFirstTab());
	}

	private static KostenRechner initKostenRechner() throws IOException {
		final Tarif tarif = new ÖtvCsvParser().parseTarif();
		log.debug("Tarif geladen");
		return new KostenRechner(tarif);
	}

	/**
	 * to be called after the initialization is done, when we can access the stage
	 * (indirectly via the AppTitle)
	 */
	void fill(final AppTitle appTitle) {
		currentProjectName.addListener((observable, oldValue, newValue) -> appTitle.updateProject(newValue));
		isSettingsChanged.addListener((observable, oldValue, newValue) -> appTitle.updateIsDirty(newValue));

		prefs.getLastProject().ifPresent(this::loadAndShow);
	}

	@FXML
	void newProject(final ActionEvent actionEvent) {
		log.trace("#newProject on {}", actionEvent);
		if (isSettingsChanged.get() && !showUnsavedChangesDialogue())
			return;

		projectTabsController.newProject();
		setCurrentProject(null);
	}

	@FXML
	void loadProject(final ActionEvent actionEvent) {
		if (isSettingsChanged.get() && !showUnsavedChangesDialogue())
			return;

		log.trace("#loadProject on {}", actionEvent);
		final FileChooser fileChooser = createAbaChooser("Projekt laden");
		final File file = fileChooser.showOpenDialog(getWindow());
		if (file == null) {
			log.debug("No project source file for loading selected");
			return;
		}
		loadAndShow(file);
	}

	private void loadAndShow(final File projectFile) {
		try {
			projectTabsController.loadProject(projectFile);
			setCurrentProject(projectFile);
		} catch (final IOException ex) {

			final String msg;
			if (ex instanceof NoSuchFileException)
				msg = String.format("Konnte Projektdatei \"%s\" nicht finden.", projectFile);
			else
				msg = String.format("Konnte Projektdatei \"%s\" nicht laden:%n%s", projectFile, ex.getMessage());
			log.error(msg, ex);
			new Alert(AlertType.ERROR, msg, ButtonType.OK).showAndWait();
			projectTabsController.initialize();
			setCurrentProject(null);
		}
	}

	void setCurrentProject(final File file) {
		log.debug("Setting current project = {}", file);
		if (file != null) {
			currentProjectName.set(file.getName());
			prefs.setLastProject(file);
		} else {
			currentProjectName.set(null);
			prefs.removeLastProject();
		}
		isSettingsChanged.set(false);
	}

	@FXML
	void saveProject(final ActionEvent actionEvent) {
		log.trace("#saveProject on {}", actionEvent);
		final File projectFile = prefs.getLastProject()
				.orElseThrow(() -> new IllegalStateException("No current project set"));
		try {
			projectTabsController.saveProject(projectFile);
			isSettingsChanged.set(false);
		} catch (final IOException ioEx) {
			log.error("Could not save project file '{}'", projectFile, ioEx);
			new Alert(AlertType.ERROR, String.format("Fehler beim Speichern von Projektdatei \"%s\"", projectFile),
					ButtonType.OK).showAndWait();
		}
	}

	@FXML
	void saveProjectAs(final ActionEvent actionEvent) {
		log.trace("#saveProjectAs on {}", actionEvent);

		final FileChooser fileChooser = createAbaChooser("Projekt speichern");
		File file = fileChooser.showSaveDialog(getWindow());
		if (file == null) {
			log.debug("No file for saving selected");
			return;
		}
		if (!file.getName().endsWith(".aba"))
			file = new File(file.getParentFile(), String.format("%s.aba", file.getName()));
		log.debug("Saving project as '{}'", file);
		setCurrentProject(file);
		saveProject(actionEvent);
	}

	private Window getWindow() {
		return topLevelPane.getScene().getWindow();
	}

	private FileChooser createAbaChooser(final String title) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Abakus-Projekte", "*.aba"));
		fileChooser.setTitle(title);
		final File dir = prefs.getLastProject().map(File::getParentFile)
				.orElse(new File(System.getProperty("user.home")));
		fileChooser.setInitialDirectory(dir);
		return fileChooser;
	}

	/**
	 * @return true the user wants to proceed (i.e., has not cancelled); false if
	 *         the user has cancelled
	 */
	boolean showUnsavedChangesDialogue() {
		if (!isSettingsChanged.get())
			return true;

		final boolean haveCurrentProject = prefs.getLastProject().isPresent();

		final ButtonType save = new ButtonType(haveCurrentProject ? "Speichern" : "Speichern...", ButtonData.YES);
		final ButtonType dontSave = new ButtonType("Nicht speichern", ButtonData.NO);
		final ButtonType cancel = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);

		final String detail = haveCurrentProject ? String.format(" an \"%s\"", currentProjectName.get()) : "";

		final Alert alert = new Alert(AlertType.CONFIRMATION,
				String.format("Möchten Sie Ihre Änderungen%s speichern?", detail), save, dontSave, cancel);
		alert.setTitle("Ungespeicherte Änderungen");
		alert.setHeaderText(null);
		((Button) alert.getDialogPane().lookupButton(save)).setDefaultButton(true);
		((Button) alert.getDialogPane().lookupButton(dontSave)).setDefaultButton(false);
		((Button) alert.getDialogPane().lookupButton(cancel)).setDefaultButton(false);

		final Optional<ButtonType> answer = alert.showAndWait();

		if (answer.isEmpty() || answer.get().equals(cancel))
			return false;

		if (answer.get().equals(save)) {
			if (haveCurrentProject)
				saveProject(null);
			else
				saveProjectAs(null);
		}
		return true;
	}

	@FXML
	public void showHelp(final ActionEvent actionEvent) {
		log.trace("#showHelp on {}", actionEvent);
		final WebView webView = new WebView();

		try {
			final String tarifString = ResourceLoader.loader.resourceAsString("ötv.csv");
			final String helpString = ResourceLoader.loader.resourceAsString("doc/main.html").replace(">>>ötv.csv<<<",
					tarifString);
			webView.getEngine().loadContent(helpString);
		} catch (final IOException e) {
			log.error("Could not load help", e);
			final Alert alert = new Alert(AlertType.ERROR,
					String.format("Die Hilfe konnte nicht geladen werden: %s", e.getMessage()));
			alert.setTitle("Interner Fehler beim Laden der Hilfe");
			alert.showAndWait();
			return;
		}

		final VBox vBox = new VBox(webView);
		VBox.setVgrow(webView, Priority.ALWAYS);

		final Stage stage = new Stage();
		stage.setTitle("Abakus-Hilfe");
		stage.setScene(new Scene(vBox, 640, 500));
		stage.show();
	}

	@FXML
	public void showVersion(final ActionEvent actionEvent) {
		log.trace("#showVersion on {}", actionEvent);

		final String disclaimer = ResourceLoader.loader.loadDisclaimer();
		if (disclaimer == null)
			return;

		final String versionString = ResourceLoader.loader.loadVersionProperties();

		final String aboutString = String.format("%s%n%n%s", versionString, disclaimer);

		final Alert info = new Alert(AlertType.INFORMATION, aboutString);
		info.setHeaderText("Abakus - Version & Lizenz");
		info.setTitle("Abakus - Version und Lizenz");
		info.showAndWait();
	}

	public boolean displayDislaimerAndAccept() {
		log.trace("#showDisclaimer");

		final String disclaimer = ResourceLoader.loader.loadDisclaimer();
		if (disclaimer == null)
			return false;

		final String frage = "Akzeptieren Sie diese Nutzungsvereinbarung?\n(\"Nein\" schließt das Programm.)";
		final Alert disclaimerConf = new Alert(AlertType.CONFIRMATION, String.format("%s%n%s", disclaimer, frage),
				ButtonType.YES, ButtonType.NO);
		((Button) disclaimerConf.getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton(true);
		((Button) disclaimerConf.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);

		disclaimerConf.setHeaderText("Abakus - Nutzungsvereinbarung");
		disclaimerConf.setTitle("Abakus - Nutzungsvereinbarung");
		final Optional<ButtonType> answer = disclaimerConf.showAndWait();
		return answer.isPresent() && answer.get().equals(ButtonType.YES);
	}

	@FXML
	void exitApp(final ActionEvent actionEvent) {
		log.trace("#exit on {}", actionEvent);
		final Window window = getWindow();
		window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
}