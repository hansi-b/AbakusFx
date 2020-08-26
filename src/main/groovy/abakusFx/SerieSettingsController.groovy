package abakusFx

import abakus.Anstellung
import abakus.Gruppe
import abakus.Stelle
import abakus.Stufe
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import groovy.transform.Immutable
import groovy.util.logging.Log4j2
import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.scene.control.*

import java.time.LocalDate
import java.time.YearMonth
import java.util.prefs.Preferences

class External {

    static final ObjectMapper objectMapper = createObjectMapper()

    static class MyCustomSerializer extends JsonSerializer<LocalDate> {
        @Override
        void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString())
        }
    }

    static class MyCustomDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            LocalDate.parse(p.getValueAsString())
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper(new YAMLFactory())
        SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null));
        testModule.addSerializer(LocalDate.class, new MyCustomSerializer())
        testModule.addDeserializer(LocalDate.class, new MyCustomDeserializer())
        om.registerModule(testModule)
        om
    }
}

@Log4j2
class SerieSettingsController {

    @Immutable
    static class Model {

        LocalDate von
        LocalDate bis
        Gruppe gruppe
        Stufe stufe
        Integer umfang
        Boolean isWeiter
        LocalDate seit
        Integer umfangSeit

        static Model of(SerieSettingsController ssc) {
            new Model(
                    von: ssc.von.getValue(),
                    bis: ssc.bis.getValue(),
                    gruppe: ssc.gruppe.getValue(),
                    stufe: ssc.stufe.getValue(),
                    umfang: ssc.umfang.getValue(),
                    isWeiter: ssc.weiter.isSelected(),
                    seit: ssc.seit.getValue(),
                    umfangSeit: ssc.umfangSeit.getValue())
        }
    }

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
    RadioButton weiter

    @FXML
    Label seitLabel
    @FXML
    DatePicker seit

    @FXML
    Label umfangSeitLabel
    @FXML
    Spinner<Integer> umfangSeit

    @FXML
    void initialize() {

        gruppe.getItems().setAll(Gruppe.values())
        // set the first toggle true to have one true
        neuOderWeiter.getToggles().first().setSelected(true)
        stufe.getItems().setAll(Stufe.values())

        [seitLabel, seit, umfangSeitLabel, umfangSeit].each {
            it.disableProperty().bind(weiter.selectedProperty().not())
        }

        Preferences prefs = Preferences.userNodeForPackage(SerieSettingsController.class)
        String serieSettings = prefs.get("seriesSettings", "")
        Model model = serieSettings ? prefsModel(serieSettings) : defaultModel()

        setState(model)
    }

    Model prefsModel(String yaml) {
        External.objectMapper.readValue(yaml, Model.class)
    }

    Model defaultModel() {
        new Model(
                von: LocalDate.now(),
                bis: LocalDate.now().plusMonths(3),
                gruppe: Gruppe.E10,
                stufe: Stufe.eins,
                umfang: 100,
                isWeiter: false,
                seit: LocalDate.now().minusMonths(6),
                umfangSeit: 100
        )
    }

    void setState(Model model) {
        von.setValue(model.von)
        bis.setValue(model.bis)
        gruppe.setValue(model.gruppe)
        stufe.setValue(model.stufe)
        umfang.getValueFactory().setValue(model.umfang)
        weiter.setSelected(model.isWeiter)
        seit.setValue(model.seit)
        umfangSeit.getValueFactory().setValue(model.umfangSeit)
    }

    Model getState() {
        Model.of(this)
    }

    public <T> void addChangeListener(ChangeListener<T> changeListener) {
        [von, bis, gruppe, stufe, umfang, seit, umfangSeit].each { it.valueProperty().addListener(changeListener) }
        weiter.selectedProperty().addListener(changeListener as ChangeListener<? super Boolean>)
    }

    Stelle getStelle() {
        def umfang = weiter.selectedProperty().value ? umfangSeit.value : umfang.value
        return Stelle.of(gruppe.value, stufe.value, umfang)
    }

    def getVonBis() {
        [von.value, bis.value].collect { YearMonth.from(it) }
    }

    def getAnstellung() {
        def beginn = YearMonth.from(weiter.selectedProperty().value ? seit.value : von.value)
        Anstellung.of(beginn, stelle, YearMonth.from(bis.value))
    }

    def stop() {
        log.info "Saving ..."
        Preferences prefs = Preferences.userNodeForPackage(SerieSettingsController.class)
        Model model = getState()

        String modelYaml = External.objectMapper.writeValueAsString(model)
        prefs.put("seriesSettings", modelYaml)
    }
}
