package abakusfx

import java.nio.file.Path

import org.testfx.util.WaitForAsyncUtils

import abakusfx.AppPrefs.PrefKeys
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import spock.lang.TempDir

/**
 * Test for issue#25
 *
 * Ensure the state of the "save" action reflects the project state
 */
public class SaveStateSpec extends AbstractAbakusSpec {

	@TempDir
	Path tempDir

	def "save on empty project is disabled, saveAs is enabled"() {
		expect:
		click('Datei')
		menuItem('#saveItem').isDisable() == true
		appController.isCurrentProjectDirty.get() == false // and same on controller level
		menuItem('#saveAsItem').isDisable() == false
	}

	def "save on freshly saved project is not dirty"() {

		given:
		withCurrentProject()

		expect:
		appController.isCurrentProjectDirty.get() == false
	}

	def "Berechnen does not enable dirty"() {

		given:
		withCurrentProject()

		when:
		clickOn("Berechnen")

		then:
		appController.isCurrentProjectDirty.get() == false
	}

	def "save after modifying setting is enabled"() {

		given:
		withCurrentProject()

		and:
		click("#gruppe")
		clickOn("E13")
		clickOn("Berechnen")

		expect:
		appController.isCurrentProjectDirty.get() == true
	}

	def "renaming tab enables dirty"() {

		given:
		withCurrentProject()

		when:
		doubleClickOn(queryNthTab(0))
		write('Renamed').type(KeyCode.ENTER)

		then:
		appController.isCurrentProjectDirty.get() == true
	}

	def "save after adding tab,saving,removing toggles dirty"() {

		given:
		def pFile = withCurrentProject()
		prefs.get(PrefKeys.lastProject) >> pFile

		when:
		click("+")

		then:
		appController.isCurrentProjectDirty.get() == true

		when:
		appController.saveProject(null)

		then:
		appController.isCurrentProjectDirty.get() == false

		when:
		def tabNode = queryNthTab(1)
		clickOn(tabNode, MouseButton.SECONDARY)
		if (Boolean.getBoolean("headless")) {
			/*
			 * hack from
			 * https://github.com/TestFX/Monocle/issues/12#issuecomment-341795874
			 */
			WaitForAsyncUtils.asyncFx { tabNode.tab.contextMenu.show(stage.scene.window) }.get()
		}
		click("Entfernen")

		then:
		appController.isCurrentProjectDirty.get() == true
	}

	def withCurrentProject(final fileName = 'project1') {
		def pFile = tempDir.resolve(fileName).toFile()
		appController.setCurrentProject(pFile)
		return pFile
	}
}