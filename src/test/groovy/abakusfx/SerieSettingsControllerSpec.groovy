package abakusfx

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import abakus.Anstellung
import abakusfx.models.ProjectModel
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.stage.Stage
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
public class SerieSettingsControllerSpec extends ApplicationSpec {

	Parent root
	SerieSettingsController controller

	@Override
	void init() throws Exception {
		FxToolkit.registerStage { new Stage() }
	}

	@Override
	public void start(Stage stage) throws Exception {
		final FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource("serieSettings.fxml"))
		root = fxmlLoader.load()
		controller = (SerieSettingsController) fxmlLoader.getController()

		Scene scene = new Scene(root)
		stage.setScene(scene)
		stage.show()
	}

	def "simple Anstellung"() {
		when:
		setPickerDate('#von', '2020-11-01')

		then:
		Thread.sleep(3000)
		controller.anstellung.am(YearMonth.of(2020, 12)) == null
	}

	def setPickerDate(pickerId, dateString) {
		def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
		def localDate = LocalDate.parse(dateString, formatter)
		lookup(pickerId).query().setValue(localDate)
	}

	@Override
	void stop() throws Exception {
		FxToolkit.hideStage()
	}
}
