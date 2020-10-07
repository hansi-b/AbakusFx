package abakusfx;

import java.util.function.Function;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * adapted from https://community.oracle.com/tech/developers/discussion/2621389/
 */
class DragSelectCell<O, T> extends TableCell<O, T> {

	private Function<T, String> formatter;

	public DragSelectCell(Function<T, String> formatter) {
		this.formatter = formatter;
		setOnDragDetected(e -> {
			startFullDrag();
			getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
		});
		setOnMouseDragEntered(e -> {
			getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());
		});
	}

	@Override
	public void updateItem(final T item, final boolean empty) {
		super.updateItem(item, empty);
		String result;
		if (empty)
			result = null;
		else if (formatter == null && item != null)
			result = item.toString();
		else
			result = formatter.apply(item);
		setText(result);
	}
}

public class DragSelectCellFactory<O, T> implements Callback<TableColumn<O, T>, TableCell<O, T>> {

	private Function<T, String> formatter;

	public DragSelectCellFactory() {
		this(null);
	}

	public DragSelectCellFactory(Function<T, String> formatter) {
		this.formatter = formatter;
	}

	@Override
	public TableCell<O, T> call(final TableColumn<O, T> col) {
		return new DragSelectCell<O, T>(formatter);
	}
}