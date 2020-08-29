package abakusFx

import abakus.KostenRechner
import abakus.ÖtvCsvParser
import groovy.util.logging.Log4j2
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Window

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
    private TextField result

    @FXML
    void initialize() {
        def tarif = new ÖtvCsvParser().parseTarif()
        rechner = new KostenRechner(tarif)
        log.info "Tarif geladen"

        calcKosten.setOnAction(a -> fillKostenTable())
        serieSettingsController.addChangeListener((_a, _b, _c) -> {
            serieTableController.clearKosten()
            clearSummenText()
        })
    }

    def fillKostenTable() {
        def (YearMonth von, YearMonth bis) = serieSettingsController.vonBis
        def moKosten = rechner.monatsKosten(serieSettingsController.anstellung, von, bis)
        serieTableController.updateKosten(moKosten)

        // TODO: extract MonatskostenCompound with methods
        def summe = moKosten.inject(euros(0)) { c, i -> c.add(i.brutto).add(i.sonderzahlung) }
        setSummenText(summe)
    }

    def setSummenText(summe) {
        def summeStr = new Converters.MoneyConverter().toString(summe)
        result.setText("Summe: $summeStr")
    }

    def clearSummenText() {
        result.setText("")
    }

    def newSeries(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#newSeries on $actionEvent"

    }

    def loadSeries(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#loadSeries on $actionEvent"

    }

    //TODO: remember previous directory
    def saveSeries(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#saveSeries on $actionEvent"

        FileChooser fileChooser = createAbaChooser()
        File file = fileChooser.showSaveDialog(topLevelPane.getScene().getWindow())
        if (file == null) {
            if (log.isDebugEnabled()) log.debug "No file for saving selected"
            return
        }
        if (!file.getName().endsWith(".aba"))
            file = new File(file.getParentFile(), String.format("%s.aba", file.getName()))
        serieSettingsController.saveSeriesToFile(file)
    }

    private FileChooser createAbaChooser() {
        FileChooser fileChooser = new FileChooser()
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Abakus-Projekte", "*.aba"))
        fileChooser.setTitle("Projekt speichern")
        fileChooser
    }

    def saveSeriesAs(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#saveSeriesAs on $actionEvent"

        Window stage = topLevelPane.getScene().getWindow()

        FileChooser fileChooser = new FileChooser()
        fileChooser.setTitle("Projekt speichern")
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Abakus-Projekte", "*.aba"))
        File file = fileChooser.showSaveDialog(stage)
        if (file == null) {
            if (log.isDebugEnabled()) log.debug "No file for saving selected"
            return
        }
        if (!file.getName().endsWith(".aba"))
            file = new File(file.getParentFile(), String.format("%s.aba", file.getName()))
        serieSettingsController.saveSeriesToFile(file)
    }

    def exit(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#exit on $actionEvent"
        Platform.exit()
    }

    def stop() {
        serieSettingsController.stop()
    }
}