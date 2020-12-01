package abakusfx;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.javamoney.moneta.Money;

import abakus.Gruppe;
import abakus.Monatskosten;
import abakus.Stufe;
import fxTools.DragSelectCellFactory;
import fxTools.CsvCopyTable;
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
		ObjectProperty<Money> kosten;

		static Kosten of(final Monatskosten mKosten) {
			final Kosten k = new Kosten();
			k.monat = new SimpleObjectProperty<>(mKosten.stichtag);
			k.gruppe = new SimpleObjectProperty<>(mKosten.stelle.gruppe);
			k.stufe = new SimpleObjectProperty<>(mKosten.stelle.stufe);
			k.umfang = new SimpleObjectProperty<>(mKosten.stelle.umfang);
			k.kosten = new SimpleObjectProperty<>(mKosten.brutto.add(mKosten.sonderzahlung));
			return k;
		}

		@Override
		public String asCsv() {
			return String.join("\t", monat.get().toString(), gruppe.get().toString(), stufe.get().toString(),
					umfang.get().toString(), moneyConverter.toString(kosten.get()));
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
	private TableColumn<Kosten, Money> kostenCol;

	private final ObservableList<Kosten> kosten = FXCollections.observableArrayList();

	@FXML
	void initialize() {
		setFactories(monatCol, k -> k.monat, ym -> ymConverter.toString(ym));
		setFactories(gruppeCol, k -> k.gruppe, null);
		setFactories(stufeCol, k -> k.stufe, null);
		setFactories(umfangCol, k -> k.umfang, null);
		setFactories(kostenCol, k -> k.kosten, m -> moneyConverter.toString(m));

		gruppeCol.prefWidthProperty().bind(monatCol.widthProperty().multiply(.8));
		stufeCol.prefWidthProperty().bind(monatCol.widthProperty().multiply(.8));

		umfangCol.prefWidthProperty().bind(monatCol.widthProperty().multiply(.6));
		kostenCol.prefWidthProperty().bind(monatCol.widthProperty().multiply(1.2));

		kostenTabelle.setPlaceholder(new Label("Keine Daten"));
		kostenTabelle.setItems(kosten);

		kostenTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		CsvCopyTable.setCsvCopy(kostenTabelle);
	}

	private static <T> void setFactories(final TableColumn<Kosten, T> col,
			final Function<Kosten, ObservableValue<T>> cellValueFac, final Function<T, String> formatter) {
		col.setCellValueFactory(cellData -> cellValueFac.apply(cellData.getValue()));
		col.setCellFactory(new DragSelectCellFactory<Kosten, T>(formatter));
	}

	/**
	 * @param kostenListe the Monatskosten which to display
	 */
	boolean updateKosten(final List<Monatskosten> kostenListe) {
		return kosten.setAll(kostenListe.stream().map(it -> Kosten.of(it)).collect(Collectors.toList()));
	}

	void clearKosten() {
		kosten.clear();
	}
}
