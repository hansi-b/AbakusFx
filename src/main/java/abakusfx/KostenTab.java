package abakusfx;

import abakus.KostenRechner;
import abakusfx.models.PersonModel;
import fxTools.RenamableTab;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

class KostenTab {

	private final RenamableTab renamableTab;
	private final KostenTabController kostenTabController;

	KostenTab(final Supplier<KostenRechner> lazyRechner, final Runnable dirtyListener,
			  final Runnable summeChangeListener) {

		renamableTab = new RenamableTab("NN");
		getTab().setClosable(false);

		final FXMLLoader loader = ResourceLoader.loader.getFxmlLoader("kostenTab.fxml");
		try {
			getTab().setContent(loader.load());
		} catch (final IOException ioEx) {
			throw new IllegalStateException("Could not initialize tab", ioEx);
		}
		kostenTabController = loader.getController();
		kostenTabController.setKostenRechner(lazyRechner);
		kostenTabController.addDirtyListener(dirtyListener);
		kostenTabController.summeProperty.addListener((obs, oldVal, newVal) -> summeChangeListener.run());
	}

	/**
	 * Has to be done after tab has been added to a pane.
	 */
	void initContextMenu(final Consumer<KostenTab> closeHandler) {
		final ContextMenu contextMenu = new ContextMenu();

		final MenuItem renameItem = new MenuItem("Umbenennen");
		renameItem.setOnAction(e -> renamableTab.editLabel());
		contextMenu.getItems().add(renameItem);

		final MenuItem closeItem = new MenuItem("Entfernen");
		closeItem.setOnAction(e -> closeHandler.accept(this));
		contextMenu.getItems().add(closeItem);
		closeItem.disableProperty().bind(Bindings.size(getTab().getTabPane().getTabs()).isEqualTo(2));

		getTab().setContextMenu(contextMenu);
	}

	Tab getTab() {
		return renamableTab.getTab();
	}

	StringProperty tabLabelProperty() {
		return renamableTab.labelProperty();
	}

	PersonÜbersicht getÜbersicht() {
		return new PersonÜbersicht(tabLabelProperty().get(), kostenTabController.getÜbersicht());
	}

	void updateSumme() {
		kostenTabController.updateResult();
	}

	void setState(final PersonModel person) {
		tabLabelProperty().set(person.name);
		kostenTabController.setState(person.series);
	}

	PersonModel getState() {
		return new PersonModel(tabLabelProperty().get(), kostenTabController.getState());
	}
}