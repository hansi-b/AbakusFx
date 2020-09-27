package abakusFx;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.javamoney.moneta.Money;

import abakus.Gruppe;
import abakus.Monatskosten;
import abakus.Stufe;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

class SerieTableController {

	static class Kosten {
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

	ObservableList<Kosten> kosten = FXCollections.observableArrayList();

	@FXML
	void initialize() {
		monatCol.setCellValueFactory(cellData -> cellData.getValue().monat);
		monatCol.setCellFactory(TextFieldTableCell.forTableColumn(new Converters.YearMonthConverter()));

		gruppeCol.setCellValueFactory(cellData -> cellData.getValue().gruppe);
		stufeCol.setCellValueFactory(cellData -> cellData.getValue().stufe);
		umfangCol.setCellValueFactory(cellData -> cellData.getValue().umfang);

		kostenCol.setCellValueFactory(cellData -> cellData.getValue().kosten);
		kostenCol.setCellFactory(TextFieldTableCell.forTableColumn(new Converters.MoneyConverter()));

		kostenTabelle.setPlaceholder(new Label("Keine Daten"));
		kostenTabelle.setItems(kosten);
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
