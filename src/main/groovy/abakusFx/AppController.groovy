package abakusFx

import abakus.Anstellung
import abakus.KostenRechner
import abakus.ÖtvCsvParser
import groovy.util.logging.Log4j2
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

import java.time.YearMonth

import static abakus.Constants.euros

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

    private KostenRechner rechner

    @FXML
    private Label summeLabel

    @FXML
    void initialize() {
        def tarif = new ÖtvCsvParser().parseTarif()
        rechner = new KostenRechner(tarif)
        log.info "Tarif geladen"

        calcKosten.setOnAction(a -> fillKostenTable())
    }

    def fillKostenTable() {
        def (YearMonth von, YearMonth bis) = serieSettingsController.vonBis
        def ans = Anstellung.of(serieSettingsController.anstellungsBeginn, serieSettingsController.stelle, bis)
        def moKosten = rechner.monatsKosten(ans, von, bis)
        serieTableController.updateKosten(moKosten)

        // TODO: extract MonatskostenCompound with methods
        def summe = moKosten.inject(euros(0)) { c, i -> c.add(i.brutto).add(i.sonderzahlung) }
        setSummenText(summe)
    }

    def setSummenText(summe) {
        def summeStr = new Converters.MoneyConverter().toString(summe)
        summeLabel.setText("Summe: $summeStr")
    }


    def exit(ActionEvent actionEvent) {
        Platform.exit()
    }
}