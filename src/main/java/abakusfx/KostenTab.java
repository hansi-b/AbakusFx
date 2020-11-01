package abakusfx;

import java.io.IOException;

import abakus.KostenRechner;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

class KostenTab {

	private final Tab myTab;
	private KostenTabController kostenTabController;

	KostenTab() {
		myTab = new Tab();
		TabTool.initTab(myTab);

		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("kostenTab.fxml"));
		try {
			final Node cont = loader.load();
			myTab.setContent(cont);
		} catch (final IOException ioEx) {
			throw new IllegalStateException("Could not initialize tab", ioEx);
		}
		kostenTabController = loader.getController();
	}

	void initContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem item = new MenuItem("Schließen");
		item.setOnAction(e -> myTab.getTabPane().getTabs().remove(myTab));
		contextMenu.getItems().add(item);
		item.disableProperty().bind(Bindings.size(myTab.getTabPane().getTabs()).isEqualTo(1));
		myTab.setContextMenu(contextMenu);
	}

	Tab getTab() {
		return myTab;
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