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

    private AppTitle appTitle
    private AppPrefs prefs

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

        // TODO: introduce model with properties
        prefs = AppPrefs.create()
    }

    /**
     * to be called after the initialization is done, when we can access the stage
     * (indirectly via the AppTitle)
     */
    void fill(AppTitle appTitle) {
        this.appTitle = appTitle
        prefs.getLastProject().ifPresent(pFile -> loadAndShow(pFile))
    }

    private loadAndShow(File projectFile) {
        serieSettingsController.loadSeries(projectFile)
        fillKostenTable()
        appTitle.projectName.setValue(projectFile.getName())
        appTitle.isDirty.set(false)
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

    def newProject(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#newProject on $actionEvent"

    }

    def loadProject(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#loadProject on $actionEvent"
        FileChooser fileChooser = createAbaChooser("Projekt laden")
        File file = fileChooser.showOpenDialog(topLevelPane.getScene().getWindow())
        if (file == null) {
            if (log.isDebugEnabled()) log.debug "No project source file for loading selected"
            return
        }
        loadAndShow(file)
        prefs.setLastProject(file)
    }

    def saveProject(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#saveProject on $actionEvent"

    }

    def saveProjectAs(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#saveProjectAs on $actionEvent"

        FileChooser fileChooser = createAbaChooser("Projekt speichern")
        File file = fileChooser.showSaveDialog(topLevelPane.getScene().getWindow())
        if (file == null) {
            if (log.isDebugEnabled()) log.debug "No file for saving selected"
            return
        }
        if (!file.getName().endsWith(".aba"))
            file = new File(file.getParentFile(), String.format("%s.aba", file.getName()))
        serieSettingsController.saveSeries(file)
        prefs.setLastProject(file)
    }

    private FileChooser createAbaChooser(String title) {
        FileChooser fileChooser = new FileChooser()
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Abakus-Projekte", "*.aba"))
        fileChooser.setTitle(title)
        prefs.getLastProject().ifPresent(f -> fileChooser.setInitialDirectory(f.getParentFile()))
        return fileChooser
    }

    def stop() {
        serieSettingsController.stop()
    }

    def exit(ActionEvent actionEvent) {
        if (log.isTraceEnabled()) log.trace "#exit on $actionEvent"
        Platform.exit()
    }
}