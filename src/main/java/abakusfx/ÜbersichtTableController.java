package abakusfx;

import static abakus.Constants.euros;
import static fxTools.TableViewTools.initDragCellCol;
import static fxTools.TableViewTools.setPrefWidth;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.javamoney.moneta.Money;

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

	public static class ÜbersichtRow implements CsvCopyTable.CsvRow {
		ObjectProperty<String> name;
		ObjectProperty<BigDecimal> umfang;

		ObjectProperty<YearMonth> vonMonat;
		ObjectProperty<String> vonStelle;

		ObjectProperty<YearMonth> bisMonat;
		ObjectProperty<String> bisStelle;

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
			this.vonStelle = new SimpleObjectProperty<>(fmtShort(vonStelle));
			this.bisMonat = new SimpleObjectProperty<>(bis);
			this.bisStelle = new SimpleObjectProperty<>(fmtShort(bisStelle));

			this.agz = new SimpleObjectProperty<>(agz);
			this.betrag = new SimpleObjectProperty<>(money);
		}

		private static String fmtShort(final Stelle s) {
			return s != null ? String.format("%s/%s", s.gruppe, s.stufe.asString()) : null;
		}

		@Override
		public String asCsv() {
			final Money money = betrag.get();
			return String.join("\t", //
					nullSafeToString(name), //
					nullSafeToString(umfang), //
					nullSafeToString(vonMonat), nullSafeToString(vonStelle), //
					nullSafeToString(bisMonat), nullSafeToString(bisStelle), //
					nullSafeToString(agz), //
					money == null ? "" : Converters.moneyConverter.toString(money));
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
	private TableColumn<ÜbersichtRow, String> vonStelleCol;
	@FXML
	private TableColumn<ÜbersichtRow, YearMonth> bisMonatCol;
	@FXML
	private TableColumn<ÜbersichtRow, String> bisStelleCol;
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
		initDragCellCol(agzCol, v -> v.agz, null);
		initDragCellCol(kostenCol, v -> v.betrag, m -> m == null ? "" : Converters.moneyConverter.toString(m));

		setPrefWidth(übersichtTabelle, nameCol, .2);
		setPrefWidth(übersichtTabelle, umfangCol, .08);
		setPrefWidth(übersichtTabelle, vonMonatCol, .12);
		setPrefWidth(übersichtTabelle, vonStelleCol, .1);
		setPrefWidth(übersichtTabelle, bisMonatCol, .12);
		setPrefWidth(übersichtTabelle, bisStelleCol, .1);
		setPrefWidth(übersichtTabelle, agzCol, .08);

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
		übersichtTabelle.getItems().setAll(personen.stream().map(ÜbersichtRow::new).collect(Collectors.toList()));
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
