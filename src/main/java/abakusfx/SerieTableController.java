/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2023 Hans Bering
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
package abakusfx;

import static org.hansib.sundries.fx.table.TableViewTools.initDragCellCol;
import static org.hansib.sundries.fx.table.TableViewTools.setPrefWidth;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.hansib.sundries.fx.table.CsvCopyTable;
import org.hansib.sundries.fx.table.TooltipCellDecorator;

import abakus.ExplainedMoney;
import abakus.Gruppe;
import abakus.Monatskosten;
import abakus.Stufe;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SerieTableController {

	static class Kosten implements CsvCopyTable.CsvRow {
		ObjectProperty<YearMonth> monat;
		ObjectProperty<Gruppe> gruppe;
		ObjectProperty<Stufe> stufe;
		ObjectProperty<BigDecimal> umfang;
		ObjectProperty<ExplainedMoney> betrag;

		static Kosten of(final Monatskosten mKosten) {
			final Kosten k = new Kosten();
			k.monat = new SimpleObjectProperty<>(mKosten.stichtag());
			k.gruppe = new SimpleObjectProperty<>(mKosten.stelle().gruppe());
			k.stufe = new SimpleObjectProperty<>(mKosten.stelle().stufe());
			k.umfang = new SimpleObjectProperty<>(mKosten.stelle().umfangPercent());
			k.betrag = new SimpleObjectProperty<>(mKosten.kosten());
			return k;
		}

		@Override
		public String asCsv() {
			return String.join("\t", monat.get().toString(), gruppe.get().toString(), stufe.get().toString(),
					umfang.get().toString(), Converters.moneyConverter.toString(betrag.get().money()));
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
		initDragCellCol(monatCol, k -> k.monat, Converters.yearMonthConverter::toString);
		initDragCellCol(gruppeCol, k -> k.gruppe, null);
		initDragCellCol(stufeCol, k -> k.stufe, null);
		initDragCellCol(umfangCol, k -> k.umfang, null);
		initDragCellCol(kostenCol, k -> k.betrag, em -> Converters.moneyConverter.toString(em.money()));
		TooltipCellDecorator.decorateColumn(kostenCol, em -> em != null ? em.explained() : null);

		setPrefWidth(kostenTabelle, monatCol, .2);
		setPrefWidth(kostenTabelle, gruppeCol, .18);
		setPrefWidth(kostenTabelle, stufeCol, .15);
		setPrefWidth(kostenTabelle, umfangCol, .15);

		final DoubleBinding colsWidth = monatCol.widthProperty().add(gruppeCol.widthProperty())//
				.add(stufeCol.widthProperty())//
				.add(umfangCol.widthProperty()).multiply(1.05);
		kostenCol.prefWidthProperty().bind(kostenTabelle.widthProperty().subtract(colsWidth));

		kostenTabelle.setPlaceholder(new Label("Keine Daten"));
		kostenTabelle.setItems(monatsKostenListe);

		kostenTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		CsvCopyTable.setCsvCopy(kostenTabelle);
	}

	/**
	 * @param kostenListe the Monatskosten which to display
	 */
	void updateKosten(final List<Monatskosten> kostenListe) {
		monatsKostenListe.setAll(kostenListe.stream().map(Kosten::of).toList());
	}

	void clearKosten() {
		monatsKostenListe.clear();
	}
}
