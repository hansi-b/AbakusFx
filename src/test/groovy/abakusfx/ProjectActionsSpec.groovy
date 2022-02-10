package abakusfx

import java.nio.file.Files

import abakusfx.AppPrefs.PrefKeys

public class ProjectActionsSpec extends AbstractAbakusSpec {

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
		appController.saveProject(null)

		then:
		prefs.get(PrefKeys.lastProject) == pPath.toString()
		Files.isRegularFile(pPath)
	}

	def "can save project under different name"() {

		given:
		def p1Path = withCurrentProject("p1")
		appController.saveProject(null)

		def p2Path = tempDir.resolve("project2.aba")
		appController.setFileToSaveAsSupplier(() -> p2Path.toFile())

		when:
		appController.saveProjectAs(null)

		then:
		prefs.get(PrefKeys.lastProject) == p2Path.toString()
		Files.isRegularFile(p2Path)
		Files.readAllLines(p2Path).equals(Files.readAllLines(p1Path))
	}
}
