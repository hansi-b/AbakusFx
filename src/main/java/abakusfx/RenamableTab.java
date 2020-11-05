package abakusfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

class RenamableTab {
	private static final Logger log = LogManager.getLogger();

	final Tab tab;
	private final TextField textField;
	private final Label label;
	private final StringProperty labelProp;

	public RenamableTab(final String initialLabel) {
		tab = new Tab();

		labelProp = new SimpleStringProperty();
		labelProp.set(initialLabel);

		label = new Label();
		label.textProperty().bind(labelProp);
		tab.setGraphic(label);

		textField = new TextField();

		textField.textProperty().isEmpty().addListener((obs, oldVal, newVal) -> {
			final ObservableList<String> styleClass = textField.getStyleClass();
			if (newVal)
				styleClass.add("error");
			else
				styleClass.remove("error");
		});

		textField.setOnAction(event -> updateOrLeave(tab, labelProp, label, textField));

		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			log.debug("focusedProperty {} {} {}", observable, oldValue, newValue);
			if (!newValue)
				updateOrLeave(tab, labelProp, label, textField);
		});

		textField.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER) && textField.getText().isEmpty())
				textField.requestFocus();
		});

		label.setOnMouseClicked(event -> {
			if (event.getClickCount() < 2)
				return;
			editLabel();
		});
	}

	void editLabel() {
		textField.setText(label.getText());
		tab.setGraphic(textField);
		textField.selectAll();
		textField.requestFocus();
	}

	void setLabel(String name) {
		labelProp.set(name);
	}

	String getLabel() {
		return labelProp.getValue();
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