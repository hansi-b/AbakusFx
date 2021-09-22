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

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import javafx.util.Duration;

public class TooltipCellDecorator<O, T> implements Callback<TableColumn<O, T>, TableCell<O, T>> {

	private final Callback<TableColumn<O, T>, TableCell<O, T>> motherFactory;
	private final Function<T, String> tipFunction;

	private TooltipCellDecorator(final Callback<TableColumn<O, T>, TableCell<O, T>> motherFactory,
			final Function<T, String> tipFunction) {
		this.motherFactory = motherFactory;
		this.tipFunction = tipFunction;
	}

	public static <S, T> void decorateColumn(final TableColumn<S, T> column, final Function<T, String> tipFunction) {
		column.setCellFactory(new TooltipCellDecorator<>(column.getCellFactory(), tipFunction));
	}

	@Override
	public TableCell<O, T> call(final TableColumn<O, T> col) {
		final TableCell<O, T> cell = motherFactory.call(col);

		cell.itemProperty().addListener((ChangeListener<T>) (observable, oldValue, newValue) -> {

			final String tipValue = tipFunction.apply(newValue);
			if (tipValue == null)
				cell.setTooltip(null);
			else {
				if (cell.getTooltip() == null)
					cell.setTooltip(createTooltip());
				cell.getTooltip().textProperty().set(tipValue);
			}
		});

		return cell;
	}

	private Tooltip createTooltip() {
		final Tooltip tooltip = new Tooltip();
		tooltip.setShowDelay(Duration.millis(700));
		tooltip.setShowDuration(Duration.minutes(1));
		return tooltip;
	}
}