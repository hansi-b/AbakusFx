package abakusfx;

import java.io.IOException;

import abakus.KostenRechner;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

class KostenTab {

	private final Tab myTab;
	private final StringProperty tabLabel;
	private final KostenTabController kostenTabController;

	KostenTab(final ReadOnlyObjectProperty<KostenRechner> kostenRechnerProp, final Runnable dirtyHandler) {
		myTab = new Tab();
		tabLabel = TabTool.initTab(myTab, "NN");

		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("kostenTab.fxml"));
		try {
			myTab.setContent(loader.load());
		} catch (final IOException ioEx) {
			throw new IllegalStateException("Could not initialize tab", ioEx);
		}
		kostenTabController = loader.getController();
		kostenTabController.setKostenRechner(kostenRechnerProp);
		kostenTabController.addDirtyListener(dirtyHandler);
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

	void setState(final PersonModel person) {
		tabLabel.set(person.name);
		kostenTabController.setState(person.series);
	}

	PersonModel getState() {
		return new PersonModel(tabLabel.getValue(), kostenTabController.getState());
	}
}