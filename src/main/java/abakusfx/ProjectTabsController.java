package abakusfx;

import java.io.File;
import java.io.IOException;

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

	SimpleObjectProperty<KostenRechner> kostenRechner = new SimpleObjectProperty<>();

	@FXML
	private TabPane projectTabs;

	private KostenTabController kostenTabController;

	private final ReadOnlyObjectWrapper<Money> projektSummeInternalProperty = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectProperty<Money> projektSummeProperty = projektSummeInternalProperty
			.getReadOnlyProperty();

	@FXML
	void initialize() throws IOException {
		// projectTabs.getTabs().stream().map(t -> t.getContent().)
//		Bindings.createObjectBinding(new Callable() {
//
//			@Override
//			public Object call() throws Exception {
//				// TODO Auto-generated method stub
//				return null;
//			}}, projectTabs.getTabs());

		// projektSummeInternalProperty.bind(Bindings.binprojectTabs.getTabs());
		projectTabs.getTabs().add(newTab());

		projectTabs.getTabs().forEach(t -> TabTool.initTab(t));
	}

	private Tab newTab() throws IOException {
		Tab t = new Tab();
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("kostenTab.fxml"));
		t.setContent(loader.load());
		kostenTabController = loader.getController();
		return t;
	}

	boolean loadAndShow(final File projectFile) {
		try {
			kostenTabController.loadSeries(projectFile);
		} catch (final IOException ioEx) {
			log.error(String.format("Could not load project file '%s'", projectFile), ioEx);
			return false;
		}
		kostenTabController.fillResult();
		return true;
	}

	public void saveSeries(final File targetFile) throws IOException {
		kostenTabController.saveSeries(targetFile);
	}

	public void reset() {
		kostenTabController.reset();
	}

	public void setKostenRechner(final KostenRechner rechner) {
		kostenTabController.setKostenRechner(rechner);
	}

	public void addDirtyListener(final Runnable listener) {
		kostenTabController.addDirtyListener(listener);
	}

	void stop() {
		kostenTabController.stop();
	}
}