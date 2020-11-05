package abakusfx;

import java.io.IOException;

import abakus.KostenRechner;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

class KostenTab {

	private final RenamableTab renamableTab;
	private final KostenTabController kostenTabController;

	KostenTab(final ReadOnlyObjectProperty<KostenRechner> kostenRechnerProp, final Runnable dirtyHandler) {
		renamableTab = new RenamableTab("NN");
		renamableTab.tab.setClosable(false);

		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("kostenTab.fxml"));
		try {
			renamableTab.tab.setContent(loader.load());
		} catch (final IOException ioEx) {
			throw new IllegalStateException("Could not initialize tab", ioEx);
		}
		kostenTabController = loader.getController();
		kostenTabController.setKostenRechner(kostenRechnerProp);
		kostenTabController.addDirtyListener(dirtyHandler);
	}

	void initContextMenu() {
		final ContextMenu contextMenu = new ContextMenu();

		final MenuItem renameItem = new MenuItem("Umbenennen");
		renameItem.setOnAction(e -> renamableTab.editLabel());
		contextMenu.getItems().add(renameItem);

		final MenuItem closeItem = new MenuItem("Schließen");
		closeItem.setOnAction(e -> renamableTab.tab.getTabPane().getTabs().remove(renamableTab.tab));
		contextMenu.getItems().add(closeItem);
		closeItem.disableProperty().bind(Bindings.size(renamableTab.tab.getTabPane().getTabs()).isEqualTo(1));

		renamableTab.tab.setContextMenu(contextMenu);
	}

	Tab getTab() {
		return renamableTab.tab;
	}

	void setState(final PersonModel person) {
		renamableTab.setLabel(person.name);
		kostenTabController.setState(person.series);
	}

	PersonModel getState() {
		return new PersonModel(renamableTab.getLabel(), kostenTabController.getState());
	}
}