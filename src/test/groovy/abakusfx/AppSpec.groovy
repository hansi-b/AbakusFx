package abakusfx

import static org.testfx.api.FxAssert.*
import static org.testfx.matcher.base.NodeMatchers.*

import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import org.testfx.matcher.control.TableViewMatchers

import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.stage.Stage

public class AppSpec extends ApplicationSpec {

	Parent root

	@Override
	void init() throws Exception {
		FxToolkit.registerStage { new Stage() }
	}

	@Override
	public void start(Stage stage) throws Exception {
		final FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource("app.fxml"))
		root = fxmlLoader.load()

		Scene scene = new Scene(root)
		stage.setScene(scene)
		stage.show()
	}

	@Override
	void stop() throws Exception {
		FxToolkit.hideStage()
	}

	def "initial start shows empty table"() {
		expect:
		verifyThat(lookup("#tabPane").query(), isEnabled())
		verifyThat('#serieSettings', isEnabled())
		verifyThat('#calcKosten', isEnabled())
		verifyThat('#serieTable', isEnabled())
		verifyThat('#kostenTabelle', TableViewMatchers.hasNumRows(0))
	}
}