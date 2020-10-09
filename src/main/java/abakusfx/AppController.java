package abakusfx;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import abakus.KostenRechner;
import abakus.Tarif;
import abakus.ÖtvCsvParser;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

public class AppController {
	static final Logger log = LogManager.getLogger();

	@FXML
	private BorderPane topLevelPane;

	@FXML
	private MenuItem saveItem;

	@FXML
	private KostenTabController kostenTabController;

	@FXML
	private TabPane tabs;

	@FXML
	private TextField result;

	private AppPrefs prefs;

	private BooleanProperty isProjectDirty;

	private StringProperty currentProjectName;

	@FXML
	void initialize() throws IOException {
		final Tarif tarif = new ÖtvCsvParser().parseTarif();
		final KostenRechner rechner = new KostenRechner(tarif);
		log.info("Tarif geladen");

		isProjectDirty = new SimpleBooleanProperty(false);
		currentProjectName = new SimpleStringProperty("");

		kostenTabController.setKostenRechner(rechner);
		kostenTabController.addDirtyListener(() -> isProjectDirty.set(true));
		result.textProperty().bind(Bindings.createStringBinding(() -> {
			final Money money = kostenTabController.summeProperty.get();
			return money == null ? "" : new Converters.MoneyConverter().toString(money);
		}, kostenTabController.summeProperty));

		saveItem.disableProperty().bind(currentProjectName.isEmpty().or(isProjectDirty.not()));

		// TODO: introduce model with properties
		prefs = AppPrefs.create();
	}

	/**
	 * to be called after the initialization is done, when we can access the stage
	 * (indirectly via the AppTitle)
	 */
	void fill(final AppTitle appTitle) {
		currentProjectName.addListener((observable, oldValue, newValue) -> {
			log.debug("project name change: {}, {}, {}'", observable, oldValue, newValue);
			appTitle.updateProject(newValue);
		});
		isProjectDirty.addListener((observable, oldValue, newValue) -> appTitle.updateIsDirty(newValue));

		prefs.getLastProject().ifPresent(this::loadAndShow);

		tabs.getTabs().forEach(t -> TabTool.initTab(t));
	}

	private void loadAndShow(final File projectFile) {
		try {
			kostenTabController.loadSeries(projectFile);
		} catch (final IOException ioEx) {
			log.error(String.format("Could not load project file '%s'", projectFile), ioEx);
			setCurrentProject(null);
			return;
		}
		kostenTabController.fillResult();
		setCurrentProject(projectFile);
	}

	void clearResult() {
		result.setText("");
	}

	@FXML
	void newProject(final ActionEvent actionEvent) {
		log.trace("#newProject on {}", actionEvent);
		kostenTabController.reset();
		setCurrentProject(null);
	}

	@FXML
	void loadProject(final ActionEvent actionEvent) {
		log.trace("#loadProject on {}", actionEvent);
		final FileChooser fileChooser = createAbaChooser("Projekt laden");
		final File file = fileChooser.showOpenDialog(topLevelPane.getScene().getWindow());
		if (file == null) {
			log.debug("No project source file for loading selected");
			return;
		}
		loadAndShow(file);
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
		isProjectDirty.set(false);
	}

	@FXML
	void saveProject(final ActionEvent actionEvent) throws IOException {
		log.trace("#saveProject on {}", actionEvent);
		kostenTabController.saveSeries(
				prefs.getLastProject().orElseThrow(() -> new IllegalStateException("No current project set")));
		isProjectDirty.set(false);
	}

	@FXML
	void saveProjectAs(final ActionEvent actionEvent) throws IOException {
		log.trace("#saveProjectAs on {}", actionEvent);

		final FileChooser fileChooser = createAbaChooser("Projekt speichern");
		File file = fileChooser.showSaveDialog(topLevelPane.getScene().getWindow());
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

	private FileChooser createAbaChooser(final String title) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Abakus-Projekte", "*.aba"));
		fileChooser.setTitle(title);
		final File dir = prefs.getLastProject().map(File::getParentFile)
				.orElse(new File(System.getProperty("user.home")));
		fileChooser.setInitialDirectory(dir);
		return fileChooser;
	}

	void stop() {
		kostenTabController.stop();
	}

	@FXML
	void exit(final ActionEvent actionEvent) {
		log.trace("#exit on {}", actionEvent);
		Platform.exit();
	}
}