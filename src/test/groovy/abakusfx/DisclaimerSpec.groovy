package abakusfx

import static org.testfx.api.FxAssert.*
import static org.testfx.matcher.base.NodeMatchers.*

import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import org.testfx.matcher.control.TableViewMatchers

import abakusfx.AppPrefs.PrefKeys
import fxTools.Windows
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import javafx.stage.Window

public class DisclaimerSpec extends AbstractAbakusSpec {

	void overrideAppPrefs() {
		prefs.put(PrefKeys.wasDisclaimerAccepted, "false")
	}

	def "starting without prior disclaimer acceptance shows disclaimer"() {

		when:
		def s = Windows.findFocusedStage()
		then:
		s.getTitle().contains("Nutzungsvereinbarung")

		when:
		click("Ja")
		then:
		APP_SPEC_WIDOW_TITLE.equals(Windows.findFocusedStage().getTitle())
	}
}