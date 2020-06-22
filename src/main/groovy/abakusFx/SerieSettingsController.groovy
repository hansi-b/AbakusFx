package abakusFx

import abakus.Gruppe
import abakus.Stelle
import abakus.Stufe
import groovy.util.logging.Log4j2
import javafx.fxml.FXML
import javafx.scene.control.*

import java.time.LocalDate

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

    @FXML
    void initialize() {

        von.setValue(LocalDate.now())
        bis.setValue(LocalDate.now().plusMonths(3))

        gruppe.getItems().setAll(Gruppe.values())
        gruppe.getSelectionModel().select(0)

        neuOderWeiter.getToggles().first().setSelected(true)

        stufe.getItems().setAll(Stufe.values())
        stufe.getSelectionModel().select(0)

        seit.setValue(LocalDate.now().minusMonths(6))

        [seitLabel, seit, umfangSeitLabel, umfangSeit].each {
            it.disableProperty().bind(weiter.selectedProperty().not())
        }
    }

    Stelle getStelle() {
        def istWeiter = weiter.selectedProperty().value
        def beginn = istWeiter ? seit.value : von.value
        def umfang = istWeiter ? umfangSeit.value : umfang.value
        return new Stelle(gruppe: gruppe.value, stufe: stufe.value, beginn: beginn, umfang: umfang)
    }

    def getVonBis() {
        [von.value, bis.value]
    }

}
