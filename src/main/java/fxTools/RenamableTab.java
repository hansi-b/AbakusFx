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
package fxTools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class RenamableTab {

	private static final String CSS_RENAMABLE_TAB_ERROR = "renamable-tab-error";
	private static final String CSS_RENAMABLE_TAB_SELECTED = "renamable-tab-selected";
	private static final String CSS_RENAMABLE_TAB_UNSELECTED = "renamable-tab-unselected";

	private static final Logger log = LogManager.getLogger();

	private final Tab tab;
	private final TextField textField;
	private final Label label;

	private final StringProperty labelProp;

	private final BooleanBinding isTabEmpty;
	private final ReadOnlyBooleanProperty isTabSelected;

	public RenamableTab(final String initialLabel) {
		tab = new Tab();

		labelProp = new SimpleStringProperty(initialLabel);

		label = new Label();
		label.textProperty().bind(labelProp);
		label.getStyleClass().add(CSS_RENAMABLE_TAB_UNSELECTED);
		tab.setGraphic(label);

		textField = new TextField();

		isTabSelected = tab.selectedProperty();
		isTabSelected.addListener((observable, oldValue, newValue) -> {
			log.trace("isTabSelected {} {} {}", observable, oldValue, newValue);
			final ObservableList<String> styleClass = label.getStyleClass();
			if (Boolean.TRUE.equals(newValue)) {
				styleClass.remove(CSS_RENAMABLE_TAB_UNSELECTED);
				if (!styleClass.contains(CSS_RENAMABLE_TAB_SELECTED))
					styleClass.add(CSS_RENAMABLE_TAB_SELECTED);
			} else {
				styleClass.remove(CSS_RENAMABLE_TAB_SELECTED);
				if (!styleClass.contains(CSS_RENAMABLE_TAB_UNSELECTED))
					styleClass.add(CSS_RENAMABLE_TAB_UNSELECTED);
			}
		});
		isTabEmpty = textField.textProperty().isEmpty();
		isTabEmpty.addListener((obs, oldVal, newVal) -> {
			final ObservableList<String> styleClass = textField.getStyleClass();
			if (Boolean.TRUE.equals(newVal)) {
				if (!styleClass.contains(CSS_RENAMABLE_TAB_ERROR))
					styleClass.add(CSS_RENAMABLE_TAB_ERROR);
			} else
				styleClass.remove(CSS_RENAMABLE_TAB_ERROR);
		});

		textField.setOnAction(event -> updateOrLeave(tab, labelProp, label, textField));

		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			log.trace("focusedProperty {} {} {}", observable, oldValue, newValue);
			if (Boolean.FALSE.equals(newValue))
				updateOrLeave(tab, labelProp, label, textField);
		});

		textField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER && textField.getText().isEmpty())
				textField.requestFocus();
		});

		label.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2)
				editLabel();
		});
	}

	public Tab getTab() {
		return tab;
	}

	public StringProperty labelProperty() {
		return labelProp;
	}

	public void editLabel() {
		textField.setText(label.getText());
		tab.setGraphic(textField);
		textField.selectAll();
		textField.requestFocus();
	}

	private static void updateOrLeave(final Tab tab, final StringProperty name, final Label label,
			final TextField textField) {
		final String text = textField.getText();
		if (text.isEmpty()) {
			textField.requestFocus();
		} else {
			name.setValue(text);
			tab.setGraphic(label);
		}
	}
}