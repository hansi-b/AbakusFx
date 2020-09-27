package abakusFx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.YearMonth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import abakus.Anstellung;
import abakus.Gruppe;
import abakus.Stelle;
import abakus.Stufe;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;

class SerieSettingsController {
	private static final Logger log = LogManager.getLogger();

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

		gruppe.getItems().setAll(Gruppe.values());
		// set the first toggle true to have one true
		neuOderWeiter.getToggles().get(0).setSelected(true);
		stufe.getItems().setAll(Stufe.values());

		seit.disableProperty().bind(weiter.selectedProperty().not());
		seitLabel.disableProperty().bind(weiter.selectedProperty().not());

		reset();
	}

	void reset() {
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
		return SeriesModel.of(this);
	}

	void addDirtyListener(final Runnable dirtyListener) {
		von.valueProperty().addListener((_ob, _ov, _nv) -> dirtyListener.run());
		bis.valueProperty().addListener((_ob, _ov, _nv) -> dirtyListener.run());
		gruppe.valueProperty().addListener((_ob, _ov, _nv) -> dirtyListener.run());
		stufe.valueProperty().addListener((_ob, _ov, _nv) -> dirtyListener.run());
		umfang.valueProperty().addListener((_ob, _ov, _nv) -> dirtyListener.run());
		seit.valueProperty().addListener((_ob, _ov, _nv) -> dirtyListener.run());
		weiter.selectedProperty().addListener((_ob, _ov, _nv) -> dirtyListener.run());
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
		// issue #19 ignore the umfangSeit for the moment
		// def umfang = weiter.selectedProperty().getValue() ? umfangSeit.getValue() :
		// umfang.getValue()
		return Stelle.of(gruppe.getValue(), stufe.getValue(), umfang.getValue());
	}

	void saveSeries(final File file) throws IOException {
		log.info("Saving project to '$file' ...");
		final String modelYaml = new ModelMapper().asString(getState());
		Files.writeString(file.toPath(), modelYaml);
	}

	void loadSeries(final File file) throws IOException {
		log.info("Loading project from '$file' ...");
		final String modelYaml = Files.readString(file.toPath());
		setState(new ModelMapper().fromString(modelYaml, SeriesModel.class));
	}

	void stop() {
		log.debug("Stopping ...");
	}
}
