package abakusfx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import com.fasterxml.jackson.core.JsonProcessingException;

import abakus.Constants;
import abakus.KostenRechner;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ProjectTabsController {
	private static final Logger log = LogManager.getLogger();

	@FXML
	private TabPane tabPane;
	private final List<KostenTab> kostenTabs = new ArrayList<>();

	private final ReadOnlyObjectWrapper<KostenRechner> kostenRechner = new ReadOnlyObjectWrapper<>();
	final SimpleObjectProperty<Runnable> dirtyListener = new SimpleObjectProperty<>();

	private final ReadOnlyObjectWrapper<Money> projektSummeInternalProperty = new ReadOnlyObjectWrapper<>();
	final ReadOnlyObjectProperty<Money> projektSummeProperty = projektSummeInternalProperty.getReadOnlyProperty();

	@FXML
	void initialize() {
		newProject();
	}

	void setKostenRechner(final KostenRechner rechner) {
		kostenRechner.setValue(rechner);
	}

	private KostenTab newKostenTab(final PersonModel person) {
		final KostenTab kostenTab = new KostenTab(//
				kostenRechner.getReadOnlyProperty(), //
				() -> dirtyListener.get().run(), //
				() -> updateSumme());

		if (person != null)
			kostenTab.setState(person);

		kostenTabs.add(kostenTab);
		tabPane.getTabs().add(tabPane.getTabs().size() - 1, kostenTab.getTab());
		kostenTab.initContextMenu(kt -> {
			kostenTabs.remove(kt);
			tabPane.getTabs().remove(kt.getTab());
		});
		return kostenTab;
	}

	void newProject() {
		reset();
		// need an initial tab here for correct dimensions
		newKostenTab(null);
		tabPane.getSelectionModel().select(0);
	}

	void saveProject(final File targetFile) throws IOException {
		log.info("Saving project to '{}' ...", targetFile);
		final String modelYaml = new ModelMapper()
				.asString(new ProjectModel(kostenTabs.stream().map(t -> t.getState()).collect(Collectors.toList())));
		Files.writeString(targetFile.toPath(), modelYaml);
	}

	void loadProject(final File projectFile) throws IOException {
		reset();
		log.info("Loading project from '{}' ...", projectFile);

		final String modelYaml = Files.readString(projectFile.toPath());
		final ProjectModel project = loadModel(modelYaml);
		project.persons.forEach(person -> newKostenTab(person));
		tabPane.getSelectionModel().select(0);
	}

	private void reset() {
		tabPane.getTabs().clear();
		kostenTabs.clear();
		initAdderTab();
	}

	private ChangeListener<Tab> adderListener = null;

	private void initAdderTab() {
		if (adderListener != null)
			tabPane.getSelectionModel().selectedItemProperty().removeListener(adderListener);

		final Tab adderTab = new Tab();
		adderTab.setClosable(false);

		final Label label = new Label("+");
		adderTab.setGraphic(label);

		adderListener = (obs, oldVal, newVal) -> {
			if (newVal != adderTab)
				return;

			final KostenTab newKostenTab = newKostenTab(null);

			tabPane.getSelectionModel().select(newKostenTab.getTab());
			newKostenTab.edit();
		};
		/*
		 * Must add the tab before the listener, otherwise the listener is triggered
		 * immediately by the tab insertion.
		 */
		tabPane.getTabs().add(adderTab);
		tabPane.getSelectionModel().selectedItemProperty().addListener(adderListener);
	}

	// @VisibleForTesting
	static ProjectModel loadModel(final String modelYaml) throws JsonProcessingException {
		try {
			return new ModelMapper().fromString(modelYaml, ProjectModel.class);
		} catch (final JsonProcessingException ex) {
			log.debug("Could not deserialize project: {}", ex.getMessage());
			log.trace(() -> ex);
			log.debug("Trying fallback to old format ...");
			final SeriesModel fromString = new ModelMapper().fromString(modelYaml, SeriesModel.class);
			return new ProjectModel(Collections.singletonList(new PersonModel("NN", fromString)));
		}
	}

	private void updateSumme() {
		Money summe = kostenTabs.stream().map(k -> k.summe().get()).filter(i -> i != null).reduce((a, b) -> a.add(b))
				.orElseGet(() -> Constants.euros(0));
		projektSummeInternalProperty.set(summe);
	}
}