package abakusfx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	final List<KostenTab> controllersByTab = new ArrayList<>();

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

	boolean loadProject(final File projectFile) {
		tabPane.getTabs().clear();
		newTab();
		final KostenTabController kostenTabController = new KostenTabController(); // controllersByTab.get(0);
		try {
			kostenTabController.loadSeries(projectFile);
		} catch (final IOException ioEx) {
			log.error(String.format("Could not load project file '%s'", projectFile), ioEx);
			return false;
		}
		kostenTabController.fillResult();
		return true;
	}

	void newTab() {
		final KostenTab kostenTab = new KostenTab();
		tabPane.getTabs().add(kostenTab.getTab());
		kostenTab.initContextMenu();

		kostenTab.setKostenRechner(kostenRechner.getReadOnlyProperty());
		kostenTab.addDirtyListener(() -> dirtyListener.get().run());
		controllersByTab.add(kostenTab);
	}

	public void newProject() {
		controllersByTab.get(0).reset();
	}

	public void saveSeries(final File targetFile) throws IOException {
		controllersByTab.get(0); // .saveSeries(targetFile);
	}
}