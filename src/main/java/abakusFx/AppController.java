package abakusFx;

import java.io.File;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import abakus.Constants;
import abakus.KostenRechner;
import abakus.Monatskosten;
import abakus.Tarif;
import abakus.ÖtvCsvParser;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

public class AppController {
	private static final Logger log = LogManager.getLogger();

	@FXML
	private BorderPane topLevelPane;

	@FXML
	private MenuItem saveItem;

	@FXML
	private SerieSettingsController serieSettingsController;
	@FXML
	private Button calcKosten;
	@FXML
	private SerieTableController serieTableController;

	private KostenRechner rechner;

	@FXML
	private TextField result;

	private AppPrefs prefs;

	private BooleanProperty isProjectDirty;

	private StringProperty currentProjectName;

	@FXML
	void initialize() throws IOException {
		final Tarif tarif = new ÖtvCsvParser().parseTarif();
		rechner = new KostenRechner(tarif);
		log.info("Tarif geladen");

		isProjectDirty = new SimpleBooleanProperty(false);
		currentProjectName = new SimpleStringProperty("");

		calcKosten.setOnAction(a -> fillResult());
		serieSettingsController.addDirtyListener(() -> {
			clearResult();
			isProjectDirty.set(true);
		});
		saveItem.disableProperty().bind(currentProjectName.isNull().or(isProjectDirty.not()));

		// TODO: introduce model with properties
		prefs = AppPrefs.create();
	}

	/**
	 * to be called after the initialization is done, when we can access the stage
	 * (indirectly via the AppTitle)
	 */
	void fill(final AppTitle appTitle) {
		currentProjectName.addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue,
					final String newValue) {
				log.debug("project name change: {}, {}, {}'", observable, oldValue, newValue);
				appTitle.updateProject(newValue);
			}
		});
		isProjectDirty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
					final Boolean newValue) {
				appTitle.updateIsDirty(newValue);
			}
		});

		prefs.getLastProject().ifPresent(pFile -> loadAndShow(pFile));
	}

	private void loadAndShow(final File projectFile) {
		try {
			serieSettingsController.loadSeries(projectFile);
		} catch (final IOException ioEx) {
			log.error("Could not load project file '$projectFile': {}", ioEx);
			setCurrentProject(null);
			return;
		}
		fillResult();
		setCurrentProject(projectFile);
	}

	void fillResult() {

		final YearMonth von = serieSettingsController.getVon();
		final YearMonth bis = serieSettingsController.getBis();

		final List<Monatskosten> moKosten = rechner.monatsKosten(serieSettingsController.getAnstellung(), von, bis);
		serieTableController.updateKosten(moKosten);

		// TODO: extract MonatskostenCompound with methods
		final Money summe = moKosten.stream().map(moKo -> moKo.brutto.add(moKo.sonderzahlung))
				.reduce(Constants.euros(0), Money::add);

		String summeStr = new Converters.MoneyConverter().toString(summe);
		result.setText(String.format("Summe: %s", summeStr));
	}

	void clearResult() {
		serieTableController.clearKosten();
		result.setText("");
	}

	@FXML
	void newProject(final ActionEvent actionEvent) {
		log.trace("#newProject on {}", actionEvent);
		serieSettingsController.reset();
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
		serieSettingsController.saveSeries(prefs.getLastProject().get());
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
		serieSettingsController.stop();
	}

	@FXML
	void exit(final ActionEvent actionEvent) {
		log.trace("#exit on {}", actionEvent);
		Platform.exit();
	}
}