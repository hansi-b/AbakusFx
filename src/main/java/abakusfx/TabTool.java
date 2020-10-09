package abakusfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class TabTool {
	static final Logger log = LogManager.getLogger();

	static void initTab(final Tab tab) {

		log.debug("handling {}", tab);
		tab.setClosable(false);
		final StringProperty labelProp = new SimpleStringProperty();
		labelProp.set("NN");

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

		tab.setContextMenu(initContextMenu(tab));
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

	private static ContextMenu initContextMenu(final Tab tab) {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem item = new MenuItem("Schließen");
		item.setOnAction(e -> tab.getTabPane().getTabs().remove(tab));
		contextMenu.getItems().add(item);
		item.disableProperty().bind(Bindings.size(tab.getTabPane().getTabs()).isEqualTo(1));
		return contextMenu;
	}
}
