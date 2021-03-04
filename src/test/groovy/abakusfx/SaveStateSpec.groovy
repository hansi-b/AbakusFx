package abakusfx

import static org.testfx.api.FxAssert.*
import static org.testfx.matcher.base.NodeMatchers.*

import java.nio.file.Files

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
import javafx.scene.input.MouseButton
import javafx.stage.Stage

/**
 * Test for issue#25
 * Ensure the state of the "save" action reflects the project state
 */
public class SaveStateSpec extends AbstractAbakusSpec {

	def tempDir = Files.createTempDirectory('abakusFxProjects')

	def "save on empty project is disabled, saveAs is enabled"() {
		expect:
		click('Datei')
		menuItem('#saveItem').isDisable() == true
		appController.isCurrentProjectDirty.get() == false // and same on controller level
		menuItem('#saveAsItem').isDisable() == false
	}

	def "save on freshly saved project is not dirty"() {

		given:
		def pFile = tempDir.resolve('p1').toFile()
		appController.setCurrentProject(pFile)

		expect:
		appController.isCurrentProjectDirty.get() == false
	}

	def "Berechnen does not enable dirty"() {

		given:
		def pFile = tempDir.resolve('p1').toFile()
		appController.setCurrentProject(pFile)

		when:
		clickOn("Berechnen")

		then:
		appController.isCurrentProjectDirty.get() == false
	}

	def "save after modifying setting is enabled"() {

		given:
		def pFile = tempDir.resolve('p1').toFile()
		appController.setCurrentProject(pFile)

		and:
		click("#gruppe")
		clickOn("E13")
		clickOn("Berechnen")

		expect:
		appController.isCurrentProjectDirty.get() == true
	}

	def "save after renaming tab enables dirty"() {

		given:
		def pFile = tempDir.resolve('p1').toFile()
		appController.setCurrentProject(pFile)

		when:
		doubleClickOn(queryNthTab(0))
		write('Renamed').type(KeyCode.ENTER)

		then:
		appController.isCurrentProjectDirty.get() == true
	}

	def "save after adding tab,saving,removing toggles dirty"() {

		given:
		def pFile = tempDir.resolve('p1').toFile()
		appController.setCurrentProject(pFile)

		when:
		click("+")

		then:
		appController.isCurrentProjectDirty.get() == true

		when:
		appController.saveProject(null)

		then:
		appController.isCurrentProjectDirty.get() == false

		when:
		clickOn(queryNthTab(1), MouseButton.SECONDARY)
		click("Schließen")

		then:
		appController.isCurrentProjectDirty.get() == true
	}
}