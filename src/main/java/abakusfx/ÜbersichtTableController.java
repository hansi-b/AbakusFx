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

	static class KostenÜbersicht implements CsvCopyTable.CsvRow {
		ObjectProperty<String> name;
		ObjectProperty<Money> betrag;

		static KostenÜbersicht of(final String name, final Money betrag) {
			final KostenÜbersicht k = new KostenÜbersicht();
			k.name = new SimpleObjectProperty<>(name);
			k.betrag = new SimpleObjectProperty<>(betrag);
			return k;
		}

		@Override
		public String asCsv() {
			final Money money = betrag.get();
			return String.join("\t", name.get(), money == null ? "" : moneyConverter.toString(money));
		}
	}

	@FXML
	private TableView<KostenÜbersicht> übersichtTabelle;

	@FXML
	private TableColumn<KostenÜbersicht, String> nameCol;
	@FXML
	private TableColumn<KostenÜbersicht, Money> kostenCol;

	private static final PseudoClass sumRowCss = PseudoClass.getPseudoClass("sum-row");
	private KostenÜbersicht sumRow;

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
		übersichtTabelle.setRowFactory(tv -> new TableRow<KostenÜbersicht>() {
			@Override
			protected void updateItem(final KostenÜbersicht k, final boolean b) {
				super.updateItem(k, b);
				pseudoClassStateChanged(sumRowCss, sumRow != null && k == sumRow);
			}
		});
	}

	private void setSortPolicyForSumRowPosition() {
		übersichtTabelle.sortPolicyProperty().set(tv -> {
			final Comparator<KostenÜbersicht> cmp = (row1, row2) -> {
				if (sumRow != null && row1 == sumRow)
					return 1;
				if (sumRow != null && row2 == sumRow)
					return -1;

				final Comparator<KostenÜbersicht> tvCmp = tv.getComparator();
				return tvCmp == null ? 0 : tvCmp.compare(row1, row2);
			};
			FXCollections.sort(tv.getItems(), cmp);
			return true;
		});
	}

	void updateItems(final List<KostenTab> tabs) {
		übersichtTabelle.getItems()
				.setAll(tabs.stream().map(t -> KostenÜbersicht.of(t.tabLabelProperty().get(), t.summe().get()))
						.collect(Collectors.toList()));
		final boolean isDirty = tabs.stream().anyMatch(t -> t.summe().get() == null);
		if (!isDirty && tabs.size() > 1) {
			final Money summe = tabs.stream().map(k -> k.summe().get()).reduce(euros(0), Money::add);
			if (summe.isGreaterThan(euros(0))) {
				sumRow = KostenÜbersicht.of("∑", summe);
				übersichtTabelle.getItems().add(sumRow);
			} else
				sumRow = null;
		}
		übersichtTabelle.sort();
	}

	private static <T> void initCol(final TableColumn<KostenÜbersicht, T> col,
			final Function<KostenÜbersicht, ObservableValue<T>> cellValueFac, final Function<T, String> formatter) {
		col.setCellValueFactory(cellData -> cellValueFac.apply(cellData.getValue()));
		col.setCellFactory(new DragSelectCellFactory<>(formatter));
	}
}
