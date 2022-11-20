package abakusfx
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import org.hansib.sundries.fx.FxResourceLoader
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import abakus.Gruppe
import abakus.Stelle
import abakus.Stufe
import javafx.scene.Parent
import javafx.stage.Stage
import spock.lang.Shared

public class SerieSettingsControllerSpec extends ApplicationSpec {
	@Shared formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	Parent root
	SerieSettingsController controller

	@Override
	void init() throws Exception {
		FxToolkit.registerStage { new Stage() }
	}

	@Override
	public void start(Stage stage) throws Exception {
		controller = new FxResourceLoader().loadFxmlToStage("serieSettings.fxml", stage)
		stage.show()
	}

	def "simple Anstellung"() {
		when:
		setPickerDate('#von', '2020-12-25')
		clickOn('#gruppe')
		clickOn('E13')
		clickOn('#stufe')
		clickOn('5')

		then:
		controller.anstellung.am(YearMonth.of(2020, 12)) == Stelle.of(Gruppe.E13, Stufe.fünf)
	}

	def setPickerDate(pickerId, dateString) {

		def localDate = LocalDate.parse(dateString, formatter)
		lookup(pickerId).query().setValue(localDate)
	}

	@Override
	void stop() throws Exception {
		FxToolkit.hideStage()
	}
}
