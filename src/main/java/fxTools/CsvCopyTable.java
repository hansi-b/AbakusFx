package fxTools;

import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class CsvCopyTable {
	private static final KeyCodeCombination defaultCopyControlKey = new KeyCodeCombination(KeyCode.C,
			KeyCombination.CONTROL_DOWN);
	private static final KeyCodeCombination defaultSelectAllControlKey = new KeyCodeCombination(KeyCode.A,
			KeyCombination.CONTROL_DOWN);

	/**
	 * a table row that can be joined to a CSV String
	 */
	public static interface CsvRow {
		String asCsv();
	}

	public static <T extends CsvRow> void setCsvCopy(final TableView<T> table) {
		table.setOnKeyReleased(e -> {
			if (defaultCopyControlKey.match(e) && table == e.getSource())
				copyCsvToClipboard(table);
			else if (defaultSelectAllControlKey.match(e) && table == e.getSource())
				table.getSelectionModel().selectAll();
		});
		addSelectCopyContextMenu(table);
	}

	private static <T extends CsvRow> void addSelectCopyContextMenu(final TableView<T> table) {
		final ContextMenu cm = new ContextMenu();
		final MenuItem selectAll = new MenuItem("Alles auswählen");
		selectAll.setOnAction(e -> table.getSelectionModel().selectAll());
		cm.getItems().add(selectAll);

		final MenuItem copySelection = new MenuItem("Auswahl kopieren");
		cm.getItems().add(copySelection);
		copySelection.setOnAction(e -> copyCsvToClipboard(table));

		table.setContextMenu(cm);
	}

	private static <T extends CsvRow> void copyCsvToClipboard(final TableView<T> table) {
		final ObservableList<T> selectedItems = table.getSelectionModel().getSelectedItems();
		final String csv = selectedItems.stream().map(T::asCsv).collect(Collectors.joining(System.lineSeparator()));

		final ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString(csv);

		Clipboard.getSystemClipboard().setContent(clipboardContent);
	}
}