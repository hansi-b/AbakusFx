package abakusfx;

import static abakus.Constants.euros;

import java.util.List;
import java.util.Objects;
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
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
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

	@FXML
	void initialize() {

		initCol(nameCol, k -> k.name, null);
		initCol(kostenCol, k -> k.betrag, m -> m == null ? "" : moneyConverter.toString(m));

		nameCol.prefWidthProperty().bind(übersichtTabelle.widthProperty().multiply(.5));

		final DoubleBinding colsWidth = nameCol.widthProperty().multiply(1.05);
		kostenCol.prefWidthProperty().bind(übersichtTabelle.widthProperty().subtract(colsWidth));

		übersichtTabelle.setPlaceholder(new Label("Keine Daten"));
		übersichtTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		übersichtTabelle.setItems(FXCollections.observableArrayList());
		CsvCopyTable.setCsvCopy(übersichtTabelle);
	}

	void setItems(final List<KostenTab> tabs) {
		übersichtTabelle.getItems()
				.setAll(tabs.stream().map(t -> KostenÜbersicht.of(t.tabLabelProperty().get(), t.summe().get()))
						.collect(Collectors.toList()));
		if (tabs.size() > 1) {
			final Money summe = tabs.stream().map(k -> k.summe().get()).filter(Objects::nonNull).reduce(euros(0),
					(a, b) -> a.add(b));
			if (summe.isGreaterThan(euros(0)))
				übersichtTabelle.getItems().add(KostenÜbersicht.of("∑", summe));
		}
	}

	private static <T> void initCol(final TableColumn<KostenÜbersicht, T> col,
			final Function<KostenÜbersicht, ObservableValue<T>> cellValueFac, final Function<T, String> formatter) {
		col.setCellValueFactory(cellData -> cellValueFac.apply(cellData.getValue()));
		col.setCellFactory(new DragSelectCellFactory<>(formatter));
		col.setSortable(false);
	}
}
