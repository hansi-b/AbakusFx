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

public class TabTool {
	static final Logger log = LogManager.getLogger();

	static StringProperty initTab(final Tab tab, String initialLabel) {

		log.debug("handling {}", tab);
		tab.setClosable(false);
		final StringProperty labelProp = new SimpleStringProperty();
		labelProp.set(initialLabel);

		final Label label = new Label();
		label.textProperty().bind(labelProp);

		tab.setGraphic(label);

		final TextField textField = new TextField();

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
			log.debug("setOnKeyPressed {}", textField.getText());
			if (e.getCode().equals(KeyCode.ENTER)) {
				if (textField.getText().isEmpty())
					textField.requestFocus();
			}
		});

		label.setOnMouseClicked(event -> {
			if (event.getClickCount() < 2)
				return;

			textField.setText(label.getText());
			tab.setGraphic(textField);
			textField.selectAll();
			textField.requestFocus();
		});

		return labelProp;
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
