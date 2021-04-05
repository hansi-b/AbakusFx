package abakusfx;

import static abakus.Constants.euros;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.javamoney.moneta.Money;

import fxTools.CsvCopyTable;
import fxTools.DragSelectCellFactory;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class ÜbersichtTableController {

	private static final Converters.MoneyConverter moneyConverter = new Converters.MoneyConverter();

	private static class ÜbersichtRow implements CsvCopyTable.CsvRow {
		ObjectProperty<String> name;
		ObjectProperty<Money> betrag;

		private ÜbersichtRow(final PersonÜbersicht p) {
			this(p.name, p.summe);
		}

		private ÜbersichtRow(final String label, final Money money) {
			name = new SimpleObjectProperty<>(label);
			betrag = new SimpleObjectProperty<>(money);
		}

		@Override
		public String asCsv() {
			final Money money = betrag.get();
			return String.join("\t", name.get(), money == null ? "" : moneyConverter.toString(money));
		}
	}

	@FXML
	private TableView<ÜbersichtRow> übersichtTabelle;

	@FXML
	private TableColumn<ÜbersichtRow, String> nameCol;
	@FXML
	private TableColumn<ÜbersichtRow, Money> kostenCol;

	private static final PseudoClass sumRowCss = PseudoClass.getPseudoClass("bottom-line");
	private ÜbersichtRow sumRow;

	@FXML
	void initialize() {

		initCol(nameCol, k -> k.name, null);
		initCol(kostenCol, k -> k.betrag, m -> m == null ? "" : moneyConverter.toString(m));

		nameCol.prefWidthProperty().bind(übersichtTabelle.widthProperty().multiply(.5));

		final DoubleBinding colsWidth = nameCol.widthProperty().multiply(1.05);
		kostenCol.prefWidthProperty().bind(übersichtTabelle.widthProperty().subtract(colsWidth));

		übersichtTabelle.setItems(FXCollections.observableArrayList());
		übersichtTabelle.setPlaceholder(new Label("Keine Daten"));
		übersichtTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		CsvCopyTable.setCsvCopy(übersichtTabelle);
		setRowFactoryForSumRowStyle();
		setSortPolicyForSumRowPosition();
	}

	private void setRowFactoryForSumRowStyle() {
		übersichtTabelle.setRowFactory(tv -> new TableRow<ÜbersichtRow>() {
			@Override
			protected void updateItem(final ÜbersichtRow k, final boolean b) {
				super.updateItem(k, b);
				pseudoClassStateChanged(sumRowCss, k != null && k == sumRow);
			}
		});
	}

	private void setSortPolicyForSumRowPosition() {
		übersichtTabelle.sortPolicyProperty().set(tv -> {
			final Comparator<ÜbersichtRow> cmp = (row1, row2) -> {
				if (sumRow != null && row1 == sumRow)
					return 1;
				if (sumRow != null && row2 == sumRow)
					return -1;

				final Comparator<ÜbersichtRow> tvCmp = tv.getComparator();
				return tvCmp == null ? 0 : tvCmp.compare(row1, row2);
			};
			FXCollections.sort(tv.getItems(), cmp);
			return true;
		});
	}

	void updateItems(final List<PersonÜbersicht> personen) {
		übersichtTabelle.getItems().setAll(personen.stream().map(ÜbersichtRow::new).collect(Collectors.toList()));
		final boolean isDirty = personen.stream().anyMatch(t -> t.summe == null);
		if (!isDirty && personen.size() > 1) {
			final Money summe = PersonÜbersicht.sumÜbersichten(personen);
			if (summe.isGreaterThan(euros(0))) {
				sumRow = new ÜbersichtRow("∑", summe);
				übersichtTabelle.getItems().add(sumRow);
			} else
				sumRow = null;
		}
		übersichtTabelle.sort();
	}

	private static <T> void initCol(final TableColumn<ÜbersichtRow, T> col,
			final Function<ÜbersichtRow, ObservableValue<T>> cellValueFac, final Function<T, String> formatter) {
		col.setCellValueFactory(cellData -> cellValueFac.apply(cellData.getValue()));
		col.setCellFactory(new DragSelectCellFactory<>(formatter));
	}
}
