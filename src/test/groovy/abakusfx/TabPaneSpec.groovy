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

public class TabPaneSpec extends AbstractAbakusSpec {

	def "first tab is selected in initial setup"() {
		expect:
		verifyThat(tabPane, isEnabled())
		tabPane.getTabs().size() == 2
		tabs[0].isSelected()
		tabs[0].graphic.text == 'NN'
	}

	def "can rename initial tab"() {
		when:
		doubleClickOn(queryNthTab(0))
		write('Scooby Doo').type(KeyCode.ENTER)

		then:
		tabs[0].graphic.text == 'Scooby Doo'
	}

	def "can add and rename tab"() {
		when:
		clickOn(queryNthTab(1))

		then:
		tabs.size() == 3
		tabs[1].isSelected()
		tabs[1].graphic.text == 'NN'

		when:
		doubleClickOn(queryNthTab(1))
		write('second').type(KeyCode.ENTER)

		then:
		tabs[0].graphic.text == 'NN'
		tabs[1].graphic.text == 'second'
	}
}