package abakusfx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

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

	public void setKostenRechner(final KostenRechner rechner) {
		kostenRechner.setValue(rechner);
	}

	KostenTab newTab() {
		final KostenTab kostenTab = new KostenTab();
		tabPane.getTabs().add(kostenTab.getTab());
		kostenTab.initContextMenu();

		kostenTab.setKostenRechner(kostenRechner.getReadOnlyProperty());
		kostenTab.addDirtyListener(() -> dirtyListener.get().run());
		kostenTabs.add(kostenTab);
		return kostenTab;
	}

	public void newProject() {
		kostenTabs.get(0).reset();
	}

	public void saveProject(final File targetFile) throws IOException {
		log.info("Saving project to '{}' ...", targetFile);
		String modelYaml = new ModelMapper().asString(getState());
		Files.writeString(targetFile.toPath(), modelYaml);
	}

	private ProjectModel getState() {
		return new ProjectModel(kostenTabs.stream().map(t -> t.getState()).collect(Collectors.toList()));
	}

	boolean loadProject(final File projectFile) throws IOException {
		clear();
		log.info("Loading project from '{}' ...", projectFile);

		final String modelYaml = Files.readString(projectFile.toPath());
		final ProjectModel project = new ModelMapper().fromString(modelYaml, ProjectModel.class);
		project.persons.forEach(person -> {
			final KostenTab newTab = newTab();
			newTab.setState(person);
		});
		return true;
	}

	private void clear() {
		tabPane.getTabs().clear();
		kostenTabs.clear();
	}
}