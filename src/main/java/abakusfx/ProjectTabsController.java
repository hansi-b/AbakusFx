/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2021  Hans Bering
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import com.fasterxml.jackson.core.JsonProcessingException;

import abakus.KostenRechner;
import abakusfx.models.PersonModel;
import abakusfx.models.ProjectModel;
import abakusfx.models.SeriesModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ProjectTabsController {
	private static final Logger log = LogManager.getLogger();

	@FXML
	private TabPane tabPane;
	private final ObservableList<KostenTab> kostenTabs = FXCollections.observableArrayList();

	private ChangeListener<Tab> adderListener = null;

	private KostenTabsChanges kostenTabChange;

	/**
	 * Keeps listeners for 1. the list of tabs and 2. the tabs' labels to allow
	 * handling on both a) list element changes (add/remove tab) and b) tab
	 * renaming.
	 */
	private static class KostenTabsChanges {
		private final ObservableList<KostenTab> kostenTabs;
		private final Map<KostenTab, ChangeListener<String>> tabListeners;

		private final Set<Consumer<List<PersonÜbersicht>>> handlers;
		private final Runnable dirtyHandler;

		private KostenTabsChanges(final ObservableList<KostenTab> kostenTabs, final Runnable dirtyHandler) {

			this.kostenTabs = kostenTabs;
			this.dirtyHandler = dirtyHandler;

			this.tabListeners = new HashMap<>();

			this.handlers = new LinkedHashSet<>();

			kostenTabs.addListener((ListChangeListener<KostenTab>) c -> triggerHandlers());
			syncHandlersToTabs();
		}

		private void triggerHandlers() {
			syncHandlersToTabs();
			final List<PersonÜbersicht> übersichten = asÜbersichten(kostenTabs);
			handlers.forEach(h -> h.accept(übersichten));
			dirtyHandler.run();
		}

		/**
		 * Keep our local handlers in sync with the actual kostenTabs: Remove the
		 * complete current (potentially outdated) set, add listeners for new set.
		 * Overhead of removing and re-adding is acceptable as long as we are dealing
		 * with only a handful of elements.
		 */
		private void syncHandlersToTabs() {
			tabListeners.forEach((tab, listener) -> tab.tabLabelProperty().removeListener(listener));
			tabListeners.clear();

			kostenTabs.forEach(t -> {
				final ChangeListener<String> labelListener = (obs, oldVal, newVal) -> triggerHandlers();
				t.tabLabelProperty().addListener(labelListener);
				tabListeners.put(t, labelListener);
			});
		}

		private void addHandler(final Consumer<List<PersonÜbersicht>> handler) {
			handlers.add(handler);
		}
	}

	private KostenRechner rechner;
	private Runnable dirtyHandler;

	private final ReadOnlyObjectWrapper<Money> projektSummeInternalProperty = new ReadOnlyObjectWrapper<>();
	final ReadOnlyObjectProperty<Money> projektSummeProperty = projektSummeInternalProperty.getReadOnlyProperty();

	private Consumer<List<PersonÜbersicht>> updateHandler;

	@FXML
	void initialize() {
		newProject();
		kostenTabChange = new KostenTabsChanges(kostenTabs, this::setToDirty);
	}

	void setChangedHandler(final Runnable dirtyHandler) {
		if (this.dirtyHandler != null)
			throw new IllegalStateException("Dirty-handler in ProjecTabsController was reset");
		this.dirtyHandler = dirtyHandler;
	}

	void setUpdateHandler(final Consumer<List<PersonÜbersicht>> updateHandler) {
		this.updateHandler = updateHandler;
		kostenTabChange.addHandler(updateHandler);
	}

	void setKostenRechner(final KostenRechner rechner) {
		this.rechner = rechner;
	}

	void newProject() {
		reset();
		// need an initial tab here for correct dimensions
		newKostenTab(null);
		focusFirstTab();
	}

	void saveProject(final File targetFile) throws IOException {
		log.info("Saving project to '{}' ...", targetFile);
		final String modelYaml = new ModelMapper()
				.asString(new ProjectModel(kostenTabs.stream().map(KostenTab::getState).collect(Collectors.toList())));
		Files.writeString(targetFile.toPath(), modelYaml);
	}

	void loadProject(final File projectFile) throws IOException {
		reset();
		log.info("Loading project from '{}' ...", projectFile);

		final String modelYaml = Files.readString(projectFile.toPath());
		final ProjectModel project = loadModel(modelYaml);
		project.persons.forEach(this::newKostenTab);
		updateSummen();
		focusFirstTab();
	}

	void focusFirstTab() {
		tabPane.getSelectionModel().select(0);
		tabPane.requestFocus();
	}

	private void reset() {
		tabPane.getTabs().clear();
		kostenTabs.clear();
		projektSummeInternalProperty.set(null);
		initAdderTab();
	}

	private void initAdderTab() {
		if (adderListener != null)
			tabPane.getSelectionModel().selectedItemProperty().removeListener(adderListener);

		final Tab adderTab = new Tab();
		adderTab.setClosable(false);

		final Label label = new Label("+");
		adderTab.setGraphic(label);

		adderListener = (obs, oldVal, newVal) -> {
			if (newVal != adderTab)
				return;

			final KostenTab newKostenTab = newKostenTab(null);
			setToDirty();

			tabPane.getSelectionModel().select(newKostenTab.getTab());
		};
		/*
		 * Must add the tab before the listener, otherwise the listener is triggered
		 * immediately by the tab insertion.
		 */
		tabPane.getTabs().add(adderTab);
		tabPane.getSelectionModel().selectedItemProperty().addListener(adderListener);
	}

	private KostenTab newKostenTab(final PersonModel person) {
		final KostenTab kostenTab = new KostenTab(//
				() -> rechner, //
				this::setToDirty, //
				this::updateSummen);

		if (person != null)
			kostenTab.setState(person);

		kostenTabs.add(kostenTab);
		tabPane.getTabs().add(tabPane.getTabs().size() - 1, kostenTab.getTab());
		kostenTab.initContextMenu(kt -> {
			kostenTabs.remove(kt);
			tabPane.getTabs().remove(kt.getTab());
			setToDirty();
		});
		return kostenTab;
	}

	void setToDirty() {
		dirtyHandler.run();
	}

	// @VisibleForTesting
	static ProjectModel loadModel(final String modelYaml) throws JsonProcessingException {
		try {
			return new ModelMapper().fromString(modelYaml, ProjectModel.class);
		} catch (final JsonProcessingException ex) {
			log.debug("Could not deserialize project: {}", ex.getMessage());
			log.trace(() -> ex);
			log.debug("Trying fallback to old format ...");
			final SeriesModel fromString = new ModelMapper().fromString(modelYaml, SeriesModel.class);
			return new ProjectModel(Collections.singletonList(new PersonModel("NN", fromString)));
		}
	}

	private static List<PersonÜbersicht> asÜbersichten(final List<KostenTab> ktList) {
		return ktList.stream().map(KostenTab::getÜbersicht).collect(Collectors.toList());
	}

	private void updateSummen() {
		kostenTabs.forEach(KostenTab::updateSumme);
		final List<PersonÜbersicht> übersichten = asÜbersichten(kostenTabs);
		projektSummeInternalProperty.set(PersonÜbersicht.sumÜbersichten(übersichten));
		updateHandler.accept(übersichten);
	}
}