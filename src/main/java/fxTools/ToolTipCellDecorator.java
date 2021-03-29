package fxTools;

import java.util.function.Function;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import javafx.util.Duration;

public class ToolTipCellDecorator<O, T> implements Callback<TableColumn<O, T>, TableCell<O, T>> {

	private final Callback<TableColumn<O, T>, TableCell<O, T>> motherFactory;
	private final Function<T, String> tipFunction;

	public ToolTipCellDecorator(final Callback<TableColumn<O, T>, TableCell<O, T>> motherFactory,
			final Function<T, String> tipFunction) {
		this.motherFactory = motherFactory;
		this.tipFunction = tipFunction;
	}

	@Override
	public TableCell<O, T> call(final TableColumn<O, T> col) {
		final TableCell<O, T> cell = motherFactory.call(col);

		final Tooltip tooltip = new Tooltip();
		tooltip.setShowDelay(Duration.millis(700));
		tooltip.setShowDuration(Duration.minutes(1));

		cell.itemProperty().addListener((ChangeListener<T>) (observable, oldValue, newValue) -> tooltip.textProperty()
				.set(tipFunction.apply(newValue)));

		cell.setTooltip(tooltip);
		return cell;
	}
}