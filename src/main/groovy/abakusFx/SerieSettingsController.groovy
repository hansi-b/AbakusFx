package abakusFx

import abakus.Anstellung
import abakus.Gruppe
import abakus.Stelle
import abakus.Stufe
import groovy.util.logging.Log4j2
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.scene.control.*

import java.time.YearMonth

@Log4j2
class SerieSettingsController {

    @FXML
    DatePicker von
    @FXML
    DatePicker bis

    @FXML
    ComboBox<Gruppe> gruppe

    @FXML
    Spinner<Integer> umfang

    @FXML
    ComboBox<Stufe> stufe

    @FXML
    ToggleGroup neuOderWeiter
    @FXML
    RadioButton weiter

    @FXML
    Label seitLabel
    @FXML
    DatePicker seit

    @FXML
    Label umfangSeitLabel
    @FXML
    Spinner<Integer> umfangSeit

    private SeriesPrefs seriesPrefs

    @FXML
    void initialize() {

        gruppe.getItems().setAll(Gruppe.values())
        // set the first toggle true to have one true
        neuOderWeiter.getToggles().first().setSelected(true)
        stufe.getItems().setAll(Stufe.values())

        [seitLabel, seit, umfangSeitLabel, umfangSeit].each {
            it.disableProperty().bind(weiter.selectedProperty().not())
        }

        seriesPrefs = SeriesPrefs.create()

        setState(readModel())
    }


    private SeriesModel readModel() {
        String modelString = seriesPrefs.getModelString()
        return modelString ? new ModelMapper().fromString(modelString, SeriesModel.class) : SeriesModel.fallback()
    }

    void setState(SeriesModel model) {
        von.setValue(model.von)
        bis.setValue(model.bis)
        gruppe.setValue(model.gruppe)
        stufe.setValue(model.stufe)
        umfang.getValueFactory().setValue(model.umfang)
        weiter.setSelected(model.isWeiter)
        seit.setValue(model.seit)
        umfangSeit.getValueFactory().setValue(model.umfangSeit)
    }

    SeriesModel getState() {
        SeriesModel.of(this)
    }

    public <T> void addChangeListener(ChangeListener<T> changeListener) {
        [von, bis, gruppe, stufe, umfang, seit, umfangSeit].each { it.valueProperty().addListener(changeListener) }
        weiter.selectedProperty().addListener(changeListener as ChangeListener<? super Boolean>)
    }

    def getVonBis() {
        [von.value, bis.value].collect { YearMonth.from(it) }
    }

    def getAnstellung() {
        def beginn = YearMonth.from(weiter.selectedProperty().value ? seit.value : von.value)
        Anstellung.of(beginn, stelle, YearMonth.from(bis.value))
    }

    private Stelle getStelle() {
        def umfang = weiter.selectedProperty().value ? umfangSeit.value : umfang.value
        return Stelle.of(gruppe.value, stufe.value, umfang)
    }

    def stop() {
        log.info "Saving ..."
        String modelYaml = new ModelMapper().asString(getState())
        seriesPrefs.setModelString(modelYaml)
    }
}
