/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2022 Hans Bering
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package abakusfx;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import abakus.KostenRechner;
import abakusfx.models.PersonModel;
import fxTools.RenamableTab;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

class KostenTab {

	private static final Logger log = LogManager.getLogger();

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
		kostenTabController.setLazies(lazyRechner, () -> updateSumme());
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
		try {
			kostenTabController.updateResult();
		} catch (IllegalArgumentException ex) {
			log.error("Error in settings", ex);
			Alert errorBox = new Alert(AlertType.ERROR);
			errorBox.setTitle("Fehler");
			errorBox.setHeaderText(
					String.format("Einstellungen für '%s' sind nicht valide:", tabLabelProperty().get()));
			errorBox.setContentText(ex.getMessage());
			errorBox.showAndWait();
		}
	}

	void setState(final PersonModel person) {
		tabLabelProperty().set(person.name);
		kostenTabController.setState(person.series);
	}

	PersonModel getState() {
		return new PersonModel(tabLabelProperty().get(), kostenTabController.getState());
	}
}