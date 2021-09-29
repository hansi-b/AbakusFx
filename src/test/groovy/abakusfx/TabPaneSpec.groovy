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
		verifyThat(projectTabsPane, isEnabled())
		projectTabsPane.getTabs().size() == 2
		projectTabs[0].isSelected()
		projectTabs[0].graphic.text == 'NN'
	}

	def "can rename initial tab"() {
		when:
		doubleClickOn(queryNthTab(0))
		write('Scooby Doo').type(KeyCode.ENTER)

		then:
		projectTabs[0].graphic.text == 'Scooby Doo'
	}

	def "can add and rename tab"() {
		when:
		clickOn(queryNthTab(1))

		then:
		projectTabs.size() == 3
		projectTabs[1].isSelected()
		projectTabs[1].graphic.text == 'NN'

		when:
		doubleClickOn(queryNthTab(1))
		write('second').type(KeyCode.ENTER)

		then:
		projectTabs[0].graphic.text == 'NN'
		projectTabs[1].graphic.text == 'second'
	}

    def "tab colour changes according to content status"() {
        given:
        doubleClickOn(queryNthTab(0))

        when:
        type(KeyCode.DELETE)

        then:
        projectTabs[0].graphic.text.isEmpty()
        projectTabs[0].graphic.styleClass.contains("error")

        when:
        write('x')

        then:
        !projectTabs[0].graphic.text.isEmpty()
        !projectTabs[0].graphic.styleClass.contains("error")
    }
}