package abakusfx

import abakusfx.AppPrefs.PrefKeys

import java.nio.file.Files
import java.nio.file.Path

import fxTools.Windows
import javafx.stage.FileChooser
import spock.lang.Ignore

@Ignore
public class ProjectActionsSpec extends AbstractAbakusSpec {

	def fileChooser = GroovyMock(FileChooser)

	def "save project throws Exception w/o prior project"() {

		when:
		appController.saveProject(null)

		then:
		def ex = thrown IllegalStateException
		ex.message == "No current project set"
	}

	def "can save project"() {

		when:
		def pPath = withCurrentProject("p1")
		then:
		!Files.exists(pPath)

		when:
		click('Datei')
		click('#saveItem')

		appController.saveProject(null)
		then:
		Files.isRegularFile(pPath)
	}

	def "can save project under different name"() {

		given:
		def p2Path = tempDir.resolve("project2")
		appController.setFileChooserFactory(t -> fileChooser)
		fileChooser.showSaveDialog(_) >> p2Path.toFile()

		when:
		def p1Path = withCurrentProject("p1")
		click('Datei')
		click('#saveItem')

		then:
		Files.isRegularFile(p2Path)
		prefs.get(PrefKeys.lastProject) == p2Path
		Files.readAllLines(p2Path).equals(Files.readAllLines(p1Path))
	}
}
