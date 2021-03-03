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
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.stage.Stage

/**
 * Test for issue#25
 * Ensure the state of the "save" action reflects the project state
 */
public class SaveStateSpec extends AbstractAbakusSpec {

	def "save on empty project is disabled"() {
		expect:
		getItemFromMenu('Datei', '#saveItem').isDisable() == true
	}
}