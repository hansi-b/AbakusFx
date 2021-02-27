package fxTools;

import java.util.function.Function;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * adapted from https://community.oracle.com/tech/developers/discussion/2621389/
 *
 * Allows for selection of cells by dragging the mouse over them.
 */
public class DragSelectCellFactory<O, T> implements Callback<TableColumn<O, T>, TableCell<O, T>> {

	private static class DragSelectCell<O, T> extends TableCell<O, T> {

		private final Function<T, String> formatter;

		DragSelectCell(final Function<T, String> formatter) {

			this.formatter = formatter;
			setOnDragDetected(e -> {
				startFullDrag();
				getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
			});
			setOnMouseDragEntered(
					e -> getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn()));
		}

		@Override
		public void updateItem(final T item, final boolean empty) {
			super.updateItem(item, empty);
			String result;
			if (empty)
				result = null;
			else if (formatter == null)
				result = item != null ? item.toString() : null;
			else
				result = formatter.apply(item);

			setText(result);
		}
	}

	private Function<T, String> formatter;

	public DragSelectCellFactory() {
		this(null);
	}

	public DragSelectCellFactory(final Function<T, String> formatter) {
		this.formatter = formatter;
	}

	@Override
	public TableCell<O, T> call(final TableColumn<O, T> col) {
		return new DragSelectCell<>(formatter);
	}
}