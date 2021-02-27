package abakusfx;

import java.io.IOException;
import java.util.function.Consumer;

import org.javamoney.moneta.Money;

import abakus.KostenRechner;
import abakusfx.models.PersonModel;
import fxTools.RenamableTab;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

class KostenTab {

	private final RenamableTab renamableTab;
	private final KostenTabController kostenTabController;

	KostenTab(final ReadOnlyObjectProperty<KostenRechner> kostenRechnerProp, final Runnable dirtyHandler,
			final Runnable summeUpdater) {

		renamableTab = new RenamableTab("NN");
		getTab().setClosable(false);

		final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("kostenTab.fxml"));
		try {
			getTab().setContent(loader.load());
		} catch (final IOException ioEx) {
			throw new IllegalStateException("Could not initialize tab", ioEx);
		}
		kostenTabController = loader.getController();
		kostenTabController.setKostenRechner(kostenRechnerProp);
		kostenTabController.addDirtyListener(dirtyHandler);
		kostenTabController.summeProperty.addListener((obs, oldVal, newVal) -> summeUpdater.run());
	}

	/**
	 * Has to be done after tab has been added to a pane.
	 */
	void initContextMenu(final Consumer<KostenTab> closeHandler) {
		final ContextMenu contextMenu = new ContextMenu();

		final MenuItem renameItem = new MenuItem("Umbenennen");
		renameItem.setOnAction(e -> renamableTab.editLabel());
		contextMenu.getItems().add(renameItem);

		final MenuItem closeItem = new MenuItem("Schließen");
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

	ReadOnlyObjectProperty<Money> summe() {
		return kostenTabController.summeProperty;
	}
	
	void updateSumme() {
		kostenTabController.fillResult();
	}

	void setState(final PersonModel person) {
		tabLabelProperty().set(person.name);
		kostenTabController.setState(person.series);
	}

	PersonModel getState() {
		return new PersonModel(tabLabelProperty().get(), kostenTabController.getState());
	}
}