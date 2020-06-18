package abakusFx

import abakus.Tarif
import abakus.ÖtvCsvParser
import groovy.util.logging.Log4j2
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

@Log4j2
class AppController {

    @FXML
    BorderPane topLevelPane

    @FXML
    SerieSettingsController serieSettingsController

    @FXML
    Label status

    Tarif tarif

    @FXML
    void initialize() {
        tarif = new ÖtvCsvParser().parseTarif()
        setStatus("Tarif geladen.")
    }

    void setStatus(String msg) {
        status.setText(msg)
        log.info msg
    }

    void exit(ActionEvent actionEvent) {
        Platform.exit()
    }
}