package abakusfx;

import java.io.IOException;

import abakus.KostenRechner;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;

class KostenTab {
	private final ProjectTabsController projectTabs;

	private final Tab myTab;
	private KostenTabController kostenTabController;

	KostenTab(final ProjectTabsController projectTabs) {
		this.projectTabs = projectTabs;
		this.myTab = newTab();
	}

	private Tab newTab() {
		final Tab tab = new Tab();
		projectTabs.projectTabs.getTabs().add(tab);
		TabTool.initTab(tab);

		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("kostenTab.fxml"));
		try {
			tab.setContent(loader.load());
		} catch (final IOException ioEx) {
			throw new IllegalStateException("Could not initialize tab", ioEx);
		}

		kostenTabController = loader.getController();
		return tab;
	}

	void setKostenRechner(final ReadOnlyObjectProperty<KostenRechner> kostenRechner) {
		kostenTabController.setKostenRechner(kostenRechner);
	}

	void addDirtyListener(final Runnable listener) {
		kostenTabController.addDirtyListener(listener);
	}

	void reset() {
		kostenTabController.reset();
	}
}