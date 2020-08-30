package abakusFx

import abakus.Anstellung
import abakus.Gruppe
import abakus.Stelle
import abakus.Stufe
import groovy.util.logging.Log4j2
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.scene.control.*

import java.nio.file.Files
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
    void initialize() {

        gruppe.getItems().setAll(Gruppe.values())
        // set the first toggle true to have one true
        neuOderWeiter.getToggles().first().setSelected(true)
        stufe.getItems().setAll(Stufe.values())

        [seitLabel, seit].each {
            it.disableProperty().bind(weiter.selectedProperty().not())
        }

        reset()
    }

    void reset() {
        setState(SeriesModel.fallback())
    }

    void setState(SeriesModel model) {
        von.setValue(model.von)
        bis.setValue(model.bis)
        gruppe.setValue(model.gruppe)
        stufe.setValue(model.stufe)
        umfang.getValueFactory().setValue(model.umfang)
        weiter.setSelected(model.isWeiter)
        seit.setValue(model.seit)
    }

    SeriesModel getState() {
        SeriesModel.of(this)
    }

    default <T> void addChangeListener(ChangeListener<T> changeListener) {
        [von, bis, gruppe, stufe, umfang, seit].each { it.valueProperty().addListener(changeListener) }
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
        //issue #19 ignore the umfangSeit for the moment
        //def umfang = weiter.selectedProperty().value ? umfangSeit.value : umfang.value
        return Stelle.of(gruppe.value, stufe.value, umfang.value)
    }

    def saveSeries(File file) {
        log.info "Saving project to '$file' ..."
        String modelYaml = new ModelMapper().asString(getState())
        Files.writeString(file.toPath(), modelYaml)
    }

    void loadSeries(File file) {
        log.info "Loading project from '$file' ..."
        def modelYaml = Files.readString(file.toPath())
        setState(new ModelMapper().fromString(modelYaml, SeriesModel.class))
    }

    def stop() {
        if (log.isDebugEnabled()) log.debug "Stopping ..."
    }
}
