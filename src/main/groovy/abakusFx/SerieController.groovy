package abakusFx

import abakus.Gruppe
import groovy.util.logging.Log4j2
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.Spinner
import javafx.scene.layout.VBox

import java.time.LocalDate

@Log4j2
class SerieController {

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
    void initialize() {

        von.setValue(LocalDate.now())

        println von.getPrefWidth()

        bis.setValue(LocalDate.now().plusMonths(3))

        println bis.getWidth()

        gruppe.getItems().setAll(Gruppe.values())
        gruppe.getSelectionModel().select(0)
    }
}