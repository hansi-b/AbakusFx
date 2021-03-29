package abakusfx;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import abakus.ExplainedMoney;
import abakus.Gruppe;
import abakus.Monatskosten;
import abakus.Stufe;
import fxTools.CsvCopyTable;
import fxTools.DragSelectCellFactory;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SerieTableController {
	private static final Converters.MoneyConverter moneyConverter = new Converters.MoneyConverter();
	private static final Converters.YearMonthConverter ymConverter = new Converters.YearMonthConverter();

	static class Kosten implements CsvCopyTable.CsvRow {
		ObjectProperty<YearMonth> monat;
		ObjectProperty<Gruppe> gruppe;
		ObjectProperty<Stufe> stufe;
		ObjectProperty<BigDecimal> umfang;
		ObjectProperty<ExplainedMoney> betrag;

		static Kosten of(final Monatskosten mKosten) {
			final Kosten k = new Kosten();
			k.monat = new SimpleObjectProperty<>(mKosten.stichtag);
			k.gruppe = new SimpleObjectProperty<>(mKosten.stelle.gruppe);
			k.stufe = new SimpleObjectProperty<>(mKosten.stelle.stufe);
			k.umfang = new SimpleObjectProperty<>(mKosten.stelle.umfangPercent);
			k.betrag = new SimpleObjectProperty<>(mKosten.kosten);
			return k;
		}

		@Override
		public String asCsv() {
			return String.join("\t", monat.get().toString(), gruppe.get().toString(), stufe.get().toString(),
					umfang.get().toString(), moneyConverter.toString(betrag.get().money()));
		}
	}

	@FXML
	private TableView<Kosten> kostenTabelle;

	@FXML
	private TableColumn<Kosten, YearMonth> monatCol;
	@FXML
	private TableColumn<Kosten, Gruppe> gruppeCol;
	@FXML
	private TableColumn<Kosten, Stufe> stufeCol;
	@FXML
	private TableColumn<Kosten, BigDecimal> umfangCol;
	@FXML
	private TableColumn<Kosten, ExplainedMoney> kostenCol;

	private final ObservableList<Kosten> monatsKostenListe = FXCollections.observableArrayList();

	@FXML
	void initialize() {
		setFactories(monatCol, k -> k.monat, ymConverter::toString);
		setFactories(gruppeCol, k -> k.gruppe, null);
		setFactories(stufeCol, k -> k.stufe, null);
		setFactories(umfangCol, k -> k.umfang, null);

		setMoneyFactories(kostenCol);

		monatCol.prefWidthProperty().bind(kostenTabelle.widthProperty().multiply(.2));
		gruppeCol.prefWidthProperty().bind(kostenTabelle.widthProperty().multiply(.18));
		stufeCol.prefWidthProperty().bind(kostenTabelle.widthProperty().multiply(.15));
		umfangCol.prefWidthProperty().bind(kostenTabelle.widthProperty().multiply(.15));

		final DoubleBinding colsWidth = monatCol.widthProperty().add(gruppeCol.widthProperty())//
				.add(stufeCol.widthProperty())//
				.add(umfangCol.widthProperty()).multiply(1.1);
		kostenCol.prefWidthProperty().bind(kostenTabelle.widthProperty().subtract(colsWidth));

		kostenTabelle.setPlaceholder(new Label("Keine Daten"));
		kostenTabelle.setItems(monatsKostenListe);

		kostenTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		CsvCopyTable.setCsvCopy(kostenTabelle);
	}

	private static void setMoneyFactories(final TableColumn<Kosten, ExplainedMoney> kostenCol) {
		kostenCol.setCellValueFactory(cellData -> ((Function<Kosten, ObservableValue<ExplainedMoney>>) k -> k.betrag)
				.apply(cellData.getValue()));
		final DragSelectCellFactory<Kosten, ExplainedMoney> fact = new DragSelectCellFactory<>(
				em -> moneyConverter.toString(em.money()));

		kostenCol.setCellFactory(fact);
	}

	private static <T> void setFactories(final TableColumn<Kosten, T> col,
			final Function<Kosten, ObservableValue<T>> cellValueFac, final Function<T, String> formatter) {
		col.setCellValueFactory(cellData -> cellValueFac.apply(cellData.getValue()));
		col.setCellFactory(new DragSelectCellFactory<>(formatter));
	}

	/**
	 * @param kostenListe the Monatskosten which to display
	 */
	void updateKosten(final List<Monatskosten> kostenListe) {
		monatsKostenListe.setAll(kostenListe.stream().map(Kosten::of).collect(Collectors.toList()));
	}

	void clearKosten() {
		monatsKostenListe.clear();
	}
}
