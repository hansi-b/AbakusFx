package fxTools;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableViewTools {

	public static <S, T> void setPrefWidth(final TableView<S> table, final TableColumn<S, T> col, final double fact) {
		col.prefWidthProperty().bind(table.widthProperty().multiply(fact));
	}

	public static <S, T> void initDragCellCol(final TableColumn<S, T> col,
			final Function<S, ObservableValue<T>> cellValueFac, final Function<T, String> formatter) {
		col.setCellValueFactory(cellData -> cellValueFac.apply(cellData.getValue()));
		col.setCellFactory(new DragSelectCellFactory<>(formatter));
	}

}
