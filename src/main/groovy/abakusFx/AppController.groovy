package abakusFx


import abakus.KostenRechner
import abakus.Stelle
import abakus.ÖtvCsvParser
import groovy.util.logging.Log4j2
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

import java.time.LocalDate

@Log4j2
class AppController {

    @FXML
    private BorderPane topLevelPane

    @FXML
    private SerieSettingsController serieSettingsController
    @FXML
    private Button calcKosten
    @FXML
    private SerieTableController serieTableController

    @FXML
    private Label status

    private KostenRechner rechner

    @FXML
    void initialize() {
        def tarif = new ÖtvCsvParser().parseTarif()
        rechner = new KostenRechner(tarif)
        setStatus("Tarif geladen")

        calcKosten.setOnAction(a -> fillKostenTable())
    }

    def fillKostenTable() {
        Stelle s = serieSettingsController.stelle
        def (LocalDate von, LocalDate bis) = serieSettingsController.vonBis
        def kl = rechner.monatsKosten(s, von, bis)
        serieTableController.kosten.setAll(kl.collect { SerieTableController.Kosten.of(it) })
    }

    def setStatus(String msg) {
        status.setText(msg)
        log.info msg
    }

    def exit(ActionEvent actionEvent) {
        Platform.exit()
    }
}