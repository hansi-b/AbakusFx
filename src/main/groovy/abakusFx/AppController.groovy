package abakusFx

import abakus.*
import groovy.util.logging.Log4j2
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

import java.time.LocalDate

@Log4j2
class AppController {

    @FXML
    BorderPane topLevelPane

    @FXML
    SerieSettingsController serieSettingsController
    @FXML
    SerieTableController serieTableController

    @FXML
    Label status

    Tarif tarif

    @FXML
    void initialize() {
        tarif = new ÖtvCsvParser().parseTarif()
        setStatus("Tarif geladen.")

        serieTableController.kosten.add(SerieTableController.Kosten.of(LocalDate.of(2020, 1, 2),
                Gruppe.E10, Stufe.fünf, BigDecimal.TEN, Constants.euros(1200)))
    }

    void setStatus(String msg) {
        status.setText(msg)
        log.info msg
    }

    void exit(ActionEvent actionEvent) {
        Platform.exit()
    }
}