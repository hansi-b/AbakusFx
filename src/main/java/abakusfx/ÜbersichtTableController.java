/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2022 Hans Bering
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

import static abakus.Constants.euros;
import static fxTools.TableViewTools.initDragCellCol;
import static fxTools.TableViewTools.setPrefWidth;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

import org.javamoney.moneta.Money;

import abakus.Constants;
import abakus.Stelle;
import fxTools.CsvCopyTable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class ÜbersichtTableController {

	private final NumberFormat numberFormat = Constants.getNumberFormat();

	static class ÜbersichtRow implements CsvCopyTable.CsvRow {

		private final NumberFormat numberFormat = Constants.getNumberFormat();

		ObjectProperty<String> name;
		ObjectProperty<BigDecimal> umfang;

		ObjectProperty<YearMonth> vonMonat;
		ObjectProperty<GruppeStufe> vonStelle;

		ObjectProperty<YearMonth> bisMonat;
		ObjectProperty<GruppeStufe> bisStelle;

		ObjectProperty<BigDecimal> agz;

		ObjectProperty<Money> betrag;

		ÜbersichtRow(final PersonÜbersicht p) {
			this(p.name, p.serie.umfang, p.serie.von, p.serie.beginn, p.serie.bis, p.serie.ende, p.serie.agz,
					p.serie.summe);
		}

		static ÜbersichtRow summe(final Money summe) {
			return new ÜbersichtRow("∑", null, null, null, null, null, null, summe);
		}

		private ÜbersichtRow(final String label, final BigDecimal umfang, final YearMonth von, final Stelle vonStelle,
				final YearMonth bis, final Stelle bisStelle, final BigDecimal agz, final Money money) {

			this.name = new SimpleObjectProperty<>(label);
			this.umfang = new SimpleObjectProperty<>(umfang);

			this.vonMonat = new SimpleObjectProperty<>(von);
			this.vonStelle = new SimpleObjectProperty<>(GruppeStufe.of(vonStelle));
			this.bisMonat = new SimpleObjectProperty<>(bis);
			this.bisStelle = new SimpleObjectProperty<>(GruppeStufe.of(bisStelle));

			this.agz = new SimpleObjectProperty<>(agz);
			this.betrag = new SimpleObjectProperty<>(money);
		}

		@Override
		public String asCsv() {
			return String.join("\t", //
					nullSafeToString(name), //
					nullSafeToString(umfang), //
					nullSafeToString(vonMonat), nullSafeToString(vonStelle), //
					nullSafeToString(bisMonat), nullSafeToString(bisStelle), //
					agz.get() == null ? "" : numberFormat.format(agz.get()), //
					betrag.get() == null ? "" : Converters.moneyConverter.toString(betrag.get()));
		}

		static <T> String nullSafeToString(final ObjectProperty<T> sop) {
			final T e = sop.get();
			return e == null ? "" : e.toString();
		}
	}

	@FXML
	private TableView<ÜbersichtRow> übersichtTabelle;
	@FXML
	private TableColumn<ÜbersichtRow, String> nameCol;
	@FXML
	private TableColumn<ÜbersichtRow, BigDecimal> umfangCol;
	@FXML
	private TableColumn<ÜbersichtRow, YearMonth> vonMonatCol;
	@FXML
	private TableColumn<ÜbersichtRow, GruppeStufe> vonStelleCol;
	@FXML
	private TableColumn<ÜbersichtRow, YearMonth> bisMonatCol;
	@FXML
	private TableColumn<ÜbersichtRow, GruppeStufe> bisStelleCol;
	@FXML
	private TableColumn<ÜbersichtRow, BigDecimal> agzCol;

	@FXML
	private TableColumn<ÜbersichtRow, Money> kostenCol;

	private static final PseudoClass sumRowCss = PseudoClass.getPseudoClass("bottom-line");
	private ÜbersichtRow sumRow;

	@FXML
	void initialize() {

		initDragCellCol(nameCol, v -> v.name, null);
		initDragCellCol(umfangCol, v -> v.umfang, null);
		initDragCellCol(vonMonatCol, v -> v.vonMonat, Converters.yearMonthConverter::toString);
		initDragCellCol(vonStelleCol, v -> v.vonStelle, null);
		initDragCellCol(bisMonatCol, v -> v.bisMonat, Converters.yearMonthConverter::toString);
		initDragCellCol(bisStelleCol, v -> v.bisStelle, null);
		initDragCellCol(agzCol, v -> v.agz, numberFormat::format);
		initDragCellCol(kostenCol, v -> v.betrag, m -> m == null ? "" : Converters.moneyConverter.toString(m));

		setPrefWidth(übersichtTabelle, nameCol, .2);
		setPrefWidth(übersichtTabelle, umfangCol, .07);
		setPrefWidth(übersichtTabelle, vonMonatCol, .115);
		setPrefWidth(übersichtTabelle, vonStelleCol, .1);
		setPrefWidth(übersichtTabelle, bisMonatCol, .115);
		setPrefWidth(übersichtTabelle, bisStelleCol, .1);
		setPrefWidth(übersichtTabelle, agzCol, .11);

		final DoubleBinding colsWidth = nameCol.widthProperty().add(umfangCol.widthProperty())//
				.add(vonMonatCol.widthProperty()).add(vonStelleCol.widthProperty())//
				.add(bisMonatCol.widthProperty()).add(bisStelleCol.widthProperty())//
				.add(agzCol.widthProperty()).multiply(1.02);

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
		übersichtTabelle.getItems().setAll(personen.stream().map(ÜbersichtRow::new).toList());
		final boolean isDirty = personen.stream().anyMatch(t -> t.serie.summe == null);
		if (!isDirty && personen.size() > 1) {
			final Money summe = PersonÜbersicht.sumÜbersichten(personen);
			if (summe.isGreaterThan(euros(0))) {
				sumRow = ÜbersichtRow.summe(summe);
				übersichtTabelle.getItems().add(sumRow);
			} else
				sumRow = null;
		}
		übersichtTabelle.sort();
	}
}
