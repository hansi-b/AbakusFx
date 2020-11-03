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

import abakus.KostenRechner;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class ProjectTabsController {
	static final Logger log = LogManager.getLogger();

	@FXML
	TabPane tabPane;

	private final List<KostenTab> kostenTabs = new ArrayList<>();

	final ReadOnlyObjectWrapper<KostenRechner> kostenRechner = new ReadOnlyObjectWrapper<>();
	final SimpleObjectProperty<Runnable> dirtyListener = new SimpleObjectProperty<>();

	private final ReadOnlyObjectWrapper<Money> projektSummeInternalProperty = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectProperty<Money> projektSummeProperty = projektSummeInternalProperty
			.getReadOnlyProperty();

	@FXML
	void initialize() {
		// need an initial tab here for correct dimensions
		newTab();
	}

	private void clear() {
		tabPane.getTabs().clear();
		kostenTabs.clear();
	}

	void setKostenRechner(final KostenRechner rechner) {
		kostenRechner.setValue(rechner);
	}

	private KostenTab newTab() {
		final KostenTab kostenTab = new KostenTab();
		tabPane.getTabs().add(kostenTab.getTab());
		kostenTab.initContextMenu();

		kostenTab.setKostenRechner(kostenRechner.getReadOnlyProperty());
		kostenTab.addDirtyListener(() -> dirtyListener.get().run());
		kostenTabs.add(kostenTab);
		return kostenTab;
	}

	void newProject() {
		clear();
		initialize();
	}

	void saveProject(final File targetFile) throws IOException {
		log.info("Saving project to '{}' ...", targetFile);
		final String modelYaml = new ModelMapper()
				.asString(new ProjectModel(kostenTabs.stream().map(t -> t.getState()).collect(Collectors.toList())));
		Files.writeString(targetFile.toPath(), modelYaml);
	}

	void loadProject(final File projectFile) throws IOException {
		clear();
		log.info("Loading project from '{}' ...", projectFile);

		final String modelYaml = Files.readString(projectFile.toPath());
		final ProjectModel project = loadModel(modelYaml);
		project.persons.forEach(person -> {
			final KostenTab newTab = newTab();
			newTab.setState(person);
		});
	}

	// @VisibleForTesting
	static ProjectModel loadModel(final String modelYaml) throws JsonProcessingException {
		try {
			return new ModelMapper().fromString(modelYaml, ProjectModel.class);
		} catch (JsonProcessingException ex) {
			log.debug("Could not deserialize project: {}", ex.getMessage());
			log.trace(() -> ex);
			log.debug("Trying fallback to old format ...");
			SeriesModel fromString = new ModelMapper().fromString(modelYaml, SeriesModel.class);
			return new ProjectModel(Collections.singletonList(new PersonModel("NN", fromString)));
		}
	}
}