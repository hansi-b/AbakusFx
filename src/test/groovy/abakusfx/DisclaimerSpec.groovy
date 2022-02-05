package abakusfx

import static org.testfx.api.FxAssert.*
import static org.testfx.matcher.base.NodeMatchers.*

import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import org.testfx.matcher.control.TableViewMatchers

import abakusfx.AppPrefs.PrefKeys
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

	void initAppPrefs() {
		prefs.put(PrefKeys._version, AppPrefs.currentVersion.name())
		prefs.put(PrefKeys.wasDisclaimerAccepted, "false")
	}

	def "starting without prior disclaimer acceptance shows disclaimer"() {

		when:
		def s = findFocusedStage()
		then:
		s.getTitle().contains("Nutzungsvereinbarung")

		when:
		click("Ja")
		then:
		!findFocusedStage().getTitle().contains("Nutzungsvereinbarung")
	}

	/**
	 * @return the last focused stage
	 */
	def findFocusedStage() {
		return Window.getWindows().stream()//
				.map(w -> (w instanceof Stage) ? (Stage) w : null) //
				.filter(w -> w != null && w.isFocused())
				.reduce((a, b) -> b).orElse(null)
	}
}