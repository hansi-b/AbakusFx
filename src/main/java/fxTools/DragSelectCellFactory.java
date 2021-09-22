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

			final String result;
			if (item == null || empty)
				result = null;
			else if (formatter == null)
				result = item.toString();
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