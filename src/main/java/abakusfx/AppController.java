package abakusfx;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import abakus.KostenRechner;
import abakus.Tarif;
import abakus.ÖtvCsvParser;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
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

	@FXML
	private TextField result;

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

		result.textProperty().bind(Bindings.createStringBinding(() -> {
			final Money money = projectTabsController.projektSummeProperty.get();
			return money == null ? "" : new Converters.MoneyConverter().toString(money);
		}, projectTabsController.projektSummeProperty));

		isCurrentProjectDirty = currentProjectName.isNotEmpty().and(isSettingsChanged);
		saveItem.disableProperty().bind(isCurrentProjectDirty.not());

		// TODO: introduce model with properties
		// e.g., store selected tab
		prefs = AppPrefs.Factory.create();

		projectTabsController.setUpdateHandler(tabs -> übersichtTableController.setItems(tabs));
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

	void clearResult() {
		result.setText("");
	}

	@FXML
	void newProject(final ActionEvent actionEvent) {
		log.trace("#newProject on {}", actionEvent);
		projectTabsController.newProject();
		setCurrentProject(null);
	}

	@FXML
	void loadProject(final ActionEvent actionEvent) {
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
				msg = String.format("Konnte Projektdatei '%s' nicht finden.", projectFile);
			else
				msg = String.format("Konnte Projektdatei '%s' nicht laden:%n%s", projectFile, ex.getMessage());
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
	void saveProject(final ActionEvent actionEvent) throws IOException {
		log.trace("#saveProject on {}", actionEvent);
		final File projectFile = prefs.getLastProject()
				.orElseThrow(() -> new IllegalStateException("No current project set"));
		projectTabsController.saveProject(projectFile);
		isSettingsChanged.set(false);
	}

	@FXML
	void saveProjectAs(final ActionEvent actionEvent) throws IOException {
		log.trace("#saveProjectAs on {}", actionEvent);

		final FileChooser fileChooser = createAbaChooser("Projekt speichern");
		File file = fileChooser.showSaveDialog(getWindow());
		if (file == null) {
			log.debug("No file for saving selected");
			return;
		}
		if (!file.getName().endsWith(".aba"))
			file = new File(file.getParentFile(), String.format("%s.aba", file.getName()));
		log.debug("Saving project as {}", file);
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

	void conditionalExit(final Event someEvent) {
		if (!isSettingsChanged.get())
			return;

		final Alert alert = new Alert(AlertType.CONFIRMATION,
				"Änderungen wurden nicht gespeichert. Möchten Sie das Programm trotzdem schließen?", ButtonType.CANCEL,
				ButtonType.OK);
		alert.setTitle("Ungespeicherte Änderungen");
		((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setDefaultButton(false);
		((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setDefaultButton(true);
		final Optional<ButtonType> answer = alert.showAndWait();

		if (answer.isPresent() && answer.get().equals(ButtonType.CANCEL))
			someEvent.consume();
	}

	@FXML
	void exitApp(final ActionEvent actionEvent) {
		log.trace("#exit on {}", actionEvent);
		final Window window = getWindow();
		window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
}