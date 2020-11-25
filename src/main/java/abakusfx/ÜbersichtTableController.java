package abakusfx;

import java.util.function.Function;

import org.javamoney.moneta.Money;

import abakus.Monatskosten;
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

public class ÜbersichtTableController {

	private static final Converters.MoneyConverter moneyConverter = new Converters.MoneyConverter();

	static class Kosten {
		ObjectProperty<String> name;
		ObjectProperty<Money> kosten;

		static Kosten of(final Monatskosten mKosten, final String name) {
			final Kosten k = new Kosten();
			k.name = new SimpleObjectProperty<>(name);
			k.kosten = new SimpleObjectProperty<>(mKosten.brutto.add(mKosten.sonderzahlung));
			return k;
		}

		String asCsv() {
			return String.join("\t", name.get(), moneyConverter.toString(kosten.get()));
		}
	}

	@FXML
	private TableView<Kosten> übersichtTabelle;

	@FXML
	private TableColumn<Kosten, String> nameCol;
	@FXML
	private TableColumn<Kosten, Money> kostenCol;

	private final ObservableList<Kosten> kosten = FXCollections.observableArrayList();

	@FXML
	void initialize() {

		setFactories(nameCol, k -> k.name, null);
		setFactories(kostenCol, k -> k.kosten, m -> moneyConverter.toString(m));

		übersichtTabelle.setPlaceholder(new Label("Keine Daten"));
		übersichtTabelle.setItems(kosten);

		übersichtTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private static <T> void setFactories(final TableColumn<Kosten, T> col,
			final Function<Kosten, ObservableValue<T>> cellValueFac, final Function<T, String> formatter) {
		col.setCellValueFactory(cellData -> cellValueFac.apply(cellData.getValue()));
		col.setCellFactory(new DragSelectCellFactory<Kosten, T>(formatter));
	}
}
