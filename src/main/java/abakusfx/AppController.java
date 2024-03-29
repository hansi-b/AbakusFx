/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2023 Hans Bering
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

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import abakus.KostenRechner;
import abakus.Tarif;
import abakus.ÖtvCsvParser;
import javafx.application.Application;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import utils.HelpUtils;

public class AppController {
	static final Logger log = LogManager.getLogger();

	private static final AppResourceLoader resourceLoader = new AppResourceLoader();

	@FXML
	private Pane topLevelPane;

	@FXML
	private MenuItem saveItem;

	@FXML
	private ProjectTabsController projectTabsController;

	@FXML
	private ÜbersichtTableController übersichtTableController;

	@FXML
	private TextField stats;

	AppPrefs prefs;

	/**
	 * whether we would want to alert the user to unsaved changes
	 */
	private BooleanProperty isSettingsChanged;

	private StringProperty currentProjectName;
	/**
	 * whether we have a project with unsaved changes
	 */
	BooleanBinding isCurrentProjectDirty;

	private Supplier<File> fileToSaveAsSupplier = this::choseFileToSaveAs;

	@FXML
	void initialize() throws IOException {

		isSettingsChanged = new SimpleBooleanProperty(false);
		currentProjectName = new SimpleStringProperty("");

		projectTabsController.setKostenRechner(initKostenRechner());
		projectTabsController.setChangedHandler(() -> isSettingsChanged.set(true));

		isCurrentProjectDirty = currentProjectName.isNotEmpty().and(isSettingsChanged);
		saveItem.disableProperty().bind(isCurrentProjectDirty.not());

		prefs = AppPrefs.create();
		if (prefs.disclaimerAccepted().isFalse())
			Platform.runLater(() -> {
				prefs.disclaimerAccepted().set(displayDislaimerAndAccept());
				if (prefs.disclaimerAccepted().isFalse()) {
					exitApp(null);
					log.info("Disclaimer was rejected");
				}
			});

		projectTabsController.setUpdateHandler(übersichtTableController::updateItems);
		Platform.runLater(() -> projectTabsController.focusFirstTab());
	}

	private static KostenRechner initKostenRechner() throws IOException {
		final Tarif tarif = new ÖtvCsvParser().parseTarif(resourceLoader.getTarifStream());
		log.debug("Tarif geladen");
		return new KostenRechner(tarif);
	}

	void setFileToSaveAsSupplier(Supplier<File> fileToSaveAsSupplier) {
		this.fileToSaveAsSupplier = fileToSaveAsSupplier;
	}

	/**
	 * to be called after the initialization is done, when we can access the stage
	 * (indirectly via the AppTitle)
	 */
	void addTitleListeners(Consumer<String> titleHandler) {
		final AppTitle appTitle = new AppTitle(titleHandler);
		currentProjectName.addListener((observable, oldValue, newValue) -> appTitle.updateProject(newValue));
		isSettingsChanged.addListener((observable, oldValue, newValue) -> appTitle.updateIsDirty(newValue));
	}

	void fill(Application.Parameters parameters) {
		List<String> unnamed = parameters.getUnnamed();
		if (!unnamed.isEmpty()) {
			log.debug("got unnamed parameters: {}", unnamed);
			loadAndShow(new File(unnamed.get(0)));
		} else {
			prefs.lastProject().get().ifPresent(this::loadAndShow);
		}
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
		final File file = choseFileToLoad();
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
			prefs.lastProject().set(file);
		} else {
			currentProjectName.set(null);
			prefs.lastProject().unset();
		}
		isSettingsChanged.set(false);
	}

	@FXML
	void saveProject(final ActionEvent actionEvent) {
		log.trace("#saveProject on {}", actionEvent);
		final File projectFile = prefs.lastProject().get()
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

		File file = fileToSaveAsSupplier.get();
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

	private File choseFileToLoad() {
		return createAbaChooser("Projekt laden").showOpenDialog(getWindow());
	}

	private File choseFileToSaveAs() {
		return createAbaChooser("Projekt speichern").showSaveDialog(getWindow());
	}

	private Window getWindow() {
		return topLevelPane.getScene().getWindow();
	}

	private FileChooser createAbaChooser(final String title) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Abakus-Projekte", "*.aba"));
		fileChooser.setTitle(title);
		final File dir = prefs.lastProject().get().map(File::getParentFile)
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

		final boolean haveCurrentProject = prefs.lastProject().isSet();

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
			final String resourceAsString = resourceLoader.resourceAsString("doc/main.html");
			final String tariffCsvString = resourceLoader.getTarifString();
			WebEngine engine = webView.getEngine();

			engine.setUserStyleSheetLocation(resourceLoader.getResourceUrl("doc/style.css"));
			engine.loadContent(
					resourceAsString.replace(">>>ötv.csv<<<", HelpUtils.csvTarifToHtmlTable(tariffCsvString)));
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

		final String disclaimer = resourceLoader.loadDisclaimer();
		if (disclaimer == null)
			return;

		final String versionString = resourceLoader.loadVersionProperties();
		final String aboutString = disclaimer.replace(">>>build.properties<<<", versionString);

		final Alert info = new Alert(AlertType.INFORMATION, aboutString);
		info.setHeaderText("Abakus - Version & Lizenz");
		info.setTitle("Abakus - Version und Lizenz");

		TextArea textArea = new TextArea(aboutString);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		VBox.setVgrow(textArea, Priority.ALWAYS);

		VBox pane = new VBox();
		pane.getChildren().add(textArea);

		info.getDialogPane().setContent(pane);
		info.setResizable(true);

		info.showAndWait();
	}

	private boolean displayDislaimerAndAccept() {
		log.trace("#showDisclaimer");

		final String disclaimer = resourceLoader.loadDisclaimer();
		if (disclaimer == null)
			return false;

		final String frage = "Akzeptieren Sie diese Nutzungsvereinbarung?\n(\"Nein\" schließt das Programm.)";
		final Alert disclaimerConf = new Alert(AlertType.CONFIRMATION, String.format("%s%n%s", disclaimer, frage),
				ButtonType.YES, ButtonType.NO);
		Button noButton = (Button) disclaimerConf.getDialogPane().lookupButton(ButtonType.NO);
		noButton.setText("Nein");
		noButton.setDefaultButton(true);
		Button yesButton = (Button) disclaimerConf.getDialogPane().lookupButton(ButtonType.YES);
		yesButton.setText("Ja");
		yesButton.setDefaultButton(false);

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