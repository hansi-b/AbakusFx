package abakusFx

import abakus.Constants
import abakus.Gruppe
import abakus.Stufe
import groovy.util.logging.Log4j2
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.converter.LocalDateStringConverter
import org.javamoney.moneta.Money

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Log4j2
class SerieTableController {

    static class Kosten {
        ObjectProperty<LocalDate> monat
        ObjectProperty<Gruppe> gruppe
        ObjectProperty<Stufe> stufe
        ObjectProperty<BigDecimal> umfang
        ObjectProperty<Money> kosten

        static Kosten of(LocalDate monat, Gruppe gruppe, Stufe stufe, BigDecimal umfang, Money kosten) {
            Kosten k = new Kosten()
            k.monat = new SimpleObjectProperty<>(monat)
            k.gruppe = new SimpleObjectProperty<>(gruppe)
            k.stufe = new SimpleObjectProperty<>(stufe)
            k.umfang = new SimpleObjectProperty<>(umfang)
            k.kosten = new SimpleObjectProperty<>(kosten)
            return k
        }
    }

    @FXML
    private TableView<Kosten> kostenTabelle

    @FXML
    private TableColumn<Kosten, LocalDate> monatCol
    @FXML
    private TableColumn<Kosten, Gruppe> gruppeCol
    @FXML
    private TableColumn<Kosten, Stufe> stufeCol
    @FXML
    private TableColumn<Kosten, BigDecimal> umfangCol
    @FXML
    private TableColumn<Kosten, Money> kostenCol

    private javafx.collections.ObservableList<Kosten> kosten = FXCollections.observableArrayList()

    @FXML
    void initialize() {
        monatCol.setCellValueFactory(cellData -> cellData.getValue().getMonat())
        monatCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter(DateTimeFormatter.ofPattern("LLL yyyy"),
                DateTimeFormatter.ofPattern("LLL yyyy"))))

        gruppeCol.setCellValueFactory(cellData -> cellData.getValue().getGruppe())
        stufeCol.setCellValueFactory(cellData -> cellData.getValue().getStufe())
        umfangCol.setCellValueFactory(cellData -> cellData.getValue().getUmfang())
        kostenCol.setCellValueFactory(cellData -> cellData.getValue().getKosten())

        kosten.addAll(Kosten.of(LocalDate.now(),
                Gruppe.E10, Stufe.drei, BigDecimal.valueOf(90), Money.of(BigDecimal.TEN, Constants.euros)))
        kostenTabelle.setItems(kosten)
    }

}
