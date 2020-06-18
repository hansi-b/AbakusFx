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
import org.javamoney.moneta.Money

import java.time.LocalDate

@Log4j2
class SerieTableController {

    static class Kosten {
        ObjectProperty<LocalDate> monat
        ObjectProperty<Gruppe> gruppe
        ObjectProperty<Stufe> stufe
        // add: Umfang
        ObjectProperty<Money> kosten

        static Kosten of(LocalDate monat, Gruppe gruppe, Stufe stufe, Money kosten) {
            Kosten k = new Kosten()
            k.monat = new SimpleObjectProperty<>(monat)
            k.gruppe = new SimpleObjectProperty<>(gruppe)
            k.stufe = new SimpleObjectProperty<>(stufe)
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
    private TableColumn<Kosten, Money> kostenCol

    private javafx.collections.ObservableList<Kosten> kosten = FXCollections.observableArrayList()

    @FXML
    void initialize() {
        monatCol.setCellValueFactory(cellData -> cellData.getValue().getMonat())
        gruppeCol.setCellValueFactory(cellData -> cellData.getValue().getGruppe())
        stufeCol.setCellValueFactory(cellData -> cellData.getValue().getStufe())
        kostenCol.setCellValueFactory(cellData -> cellData.getValue().getKosten())

        kosten.addAll(Kosten.of(LocalDate.now(),
                Gruppe.E10, Stufe.drei, Money.of(BigDecimal.TEN, Constants.euros)))
        kostenTabelle.setItems(kosten)
    }

}
