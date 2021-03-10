package abakusfx;

import java.time.LocalDate;
import java.time.YearMonth;

import abakus.Anstellung;
import abakus.Gruppe;
import abakus.Stelle;
import abakus.Stufe;
import abakusfx.models.SeriesModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;

public class SerieSettingsController {
	@FXML
	DatePicker von;
	@FXML
	DatePicker bis;

	@FXML
	ComboBox<Gruppe> gruppe;

	@FXML
	Spinner<Integer> umfang;

	@FXML
	ComboBox<Stufe> stufe;

	@FXML
	ToggleGroup neuOderWeiter;
	@FXML
	RadioButton weiter;

	@FXML
	Label seitLabel;
	@FXML
	DatePicker seit;

	@FXML
	void initialize() {
		von.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.getDayOfMonth() != 1)
				von.valueProperty().setValue(newValue.withDayOfMonth(1));
		});
		bis.valueProperty().addListener((observable, oldValue, newValue) -> {
			int lastDay = newValue.lengthOfMonth();
			if (newValue.getDayOfMonth() != lastDay)
				bis.valueProperty().setValue(newValue.withDayOfMonth(lastDay));
		});

		gruppe.getItems().setAll(Gruppe.values());
		// set the first toggle true to have one true
		neuOderWeiter.getToggles().get(0).setSelected(true);
		stufe.getItems().setAll(Stufe.values());

		seit.disableProperty().bind(weiter.selectedProperty().not());
		seitLabel.disableProperty().bind(weiter.selectedProperty().not());

		setState(SeriesModel.fallback());
	}

	void setState(final SeriesModel model) {
		von.setValue(model.von);
		bis.setValue(model.bis);
		gruppe.setValue(model.gruppe);
		stufe.setValue(model.stufe);
		umfang.getValueFactory().setValue(model.umfang);
		weiter.setSelected(model.isWeiter);
		seit.setValue(model.seit);
	}

	SeriesModel getState() {
		return new SeriesModel(von.getValue(), bis.getValue(), gruppe.getValue(), stufe.getValue(), umfang.getValue(),
				weiter.isSelected(), seit.getValue()
				// umfangSeit: ssc.umfangSeit.getValue()
		);
	}

	void addDirtyListener(final Runnable dirtyListener) {
		von.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		bis.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		gruppe.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		stufe.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		umfang.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		seit.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		weiter.selectedProperty().addListener((ob, ov, nv) -> dirtyListener.run());
	}

	YearMonth getVon() {
		return YearMonth.from(von.getValue());
	}

	YearMonth getBis() {
		return YearMonth.from(bis.getValue());
	}

	Anstellung getAnstellung() {
		final YearMonth beginn = YearMonth
				.from(weiter.selectedProperty().getValue() ? seit.getValue() : von.getValue());
		return Anstellung.of(beginn, getStelle(), YearMonth.from(bis.getValue()));
	}

	private Stelle getStelle() {
		// TODO issue #19 ignore the umfangSeit for the moment
		// def umfang = weiter.selectedProperty().getValue() ? umfangSeit.getValue() :
		// umfang.getValue()
		return Stelle.of(gruppe.getValue(), stufe.getValue(), umfang.getValue());
	}
}
