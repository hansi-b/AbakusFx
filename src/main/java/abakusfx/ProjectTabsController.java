package abakusfx;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import abakus.KostenRechner;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ProjectTabsController {
	static final Logger log = LogManager.getLogger();

	@FXML
	private TabPane projectTabs;

	private final Map<Tab, KostenTabController> controllersByTab = new HashMap<>();

	final ReadOnlyObjectWrapper<KostenRechner> kostenRechner = new ReadOnlyObjectWrapper<>();
	final SimpleObjectProperty<Runnable> dirtyListener = new SimpleObjectProperty<>();

	private final ReadOnlyObjectWrapper<Money> projektSummeInternalProperty = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectProperty<Money> projektSummeProperty = projektSummeInternalProperty
			.getReadOnlyProperty();

	@FXML
	void initialize() {
		newTab();
		// currently empty
	}

	public void setKostenRechner(final KostenRechner rechner) {
		kostenRechner.setValue(rechner);
	}

	private Tab newTab() {
		final Tab tab = new Tab();
		projectTabs.getTabs().add(tab);
		TabTool.initTab(tab);

		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("kostenTab.fxml"));
		try {
			tab.setContent(loader.load());
		} catch (final IOException ioEx) {
			throw new IllegalStateException("Could not initialize tab", ioEx);
		}

		final KostenTabController kostenTabController = loader.getController();
		kostenTabController.setKostenRechner(kostenRechner.getReadOnlyProperty());
		kostenTabController.addDirtyListener(dirtyListener.get());
		controllersByTab.put(tab, kostenTabController);
		return tab;
	}

	boolean loadAndShow(final File projectFile) {
		projectTabs.getTabs().clear();
		newTab();
		final KostenTabController kostenTabController = controllersByTab.get(projectTabs.getTabs().get(0));
		try {
			kostenTabController.loadSeries(projectFile);
		} catch (final IOException ioEx) {
			log.error(String.format("Could not load project file '%s'", projectFile), ioEx);
			return false;
		}
		kostenTabController.fillResult();
		return true;
	}

	public void newProject() {
		controllersByTab.get(projectTabs.getTabs().get(0)).reset();
	}

	public void saveSeries(final File targetFile) throws IOException {
		controllersByTab.get(projectTabs.getTabs().get(0)).saveSeries(targetFile);
	}
}