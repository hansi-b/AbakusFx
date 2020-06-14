package abakusFx

import abakus.Gruppe
import abakus.Stufe
import groovy.util.logging.Log4j2
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.Spinner
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.VBox
import javafx.util.StringConverter

import java.time.LocalDate

@Log4j2
class SerieController {

    private static class StufeConverter extends StringConverter<Stufe> {

        @Override
        String toString(Stufe stufe) {
            stufe.asString()
        }

        @Override
        Stufe fromString(String string) {
            Stufe.fromString(string)
        }
    }
    @FXML
    VBox seriePane

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
    void initialize() {

        von.setValue(LocalDate.now())
        bis.setValue(LocalDate.now().plusMonths(3))

        gruppe.getItems().setAll(Gruppe.values())
        gruppe.getSelectionModel().select(0)

        stufe.setConverter(new StufeConverter())
        stufe.getItems().setAll(Stufe.values())
        stufe.getSelectionModel().select(0)
    }
}
