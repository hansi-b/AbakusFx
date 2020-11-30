package abakusfx;

import java.util.function.Function;

import org.javamoney.moneta.Money;

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

	static class KostenÜbersicht {
		ObjectProperty<String> name;
		ObjectProperty<Money> betrag;

		static KostenÜbersicht of(final String name, final Money betrag) {
			final KostenÜbersicht k = new KostenÜbersicht();
			k.name = new SimpleObjectProperty<>(name);
			k.betrag = new SimpleObjectProperty<>(betrag);
			return k;
		}

		String asCsv() {
			return String.join("\t", name.get(), moneyConverter.toString(betrag.get()));
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

		setFactories(nameCol, k -> k.name, null);

		setFactories(kostenCol, k -> k.betrag, m -> moneyConverter.toString(m));

		übersichtTabelle.setPlaceholder(new Label("Keine Daten"));

		übersichtTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		übersichtTabelle.setItems(FXCollections.observableArrayList());
	}

	private static <T> void setFactories(final TableColumn<KostenÜbersicht, T> col,
			final Function<KostenÜbersicht, ObservableValue<T>> cellValueFac, final Function<T, String> formatter) {
		col.setCellValueFactory(cellData -> cellValueFac.apply(cellData.getValue()));
		col.setCellFactory(new DragSelectCellFactory<KostenÜbersicht, T>(formatter));
	}
}
