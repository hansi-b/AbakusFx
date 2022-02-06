package abakusfx

import java.nio.file.Path

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import abakusfx.AppPrefs.PrefKeys
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.MouseButton
import javafx.stage.Stage
import spock.lang.TempDir
import utils.EnumPrefs
import utils.InMemoryPrefs

/**
 * Basis boiler-plate class to derive FX-test-classes from.
 * Provides fields like the project tabs, and utility accessors.
 */
public class AbstractAbakusSpec extends ApplicationSpec {

	static final Logger log = LogManager.getLogger(AbstractAbakusSpec)

	static final String APP_SPEC_WIDOW_TITLE = 'Abakus Spec Window'

	@TempDir
	Path tempDir

	Parent root
	Stage stage

	AppController appController
	EnumPrefs<PrefKeys> prefs = new InMemoryPrefs<PrefKeys>()

	TabPane projectTabsPane
	ObservableList<Tab> projectTabs

	def setupSpec() {

		if (Boolean.getBoolean("headless")) {
			log.info ">>> HEADLESS MODE"

			System.setProperty("testfx.robot", "glass")
			System.setProperty("testfx.headless", "true")
			System.setProperty("prism.order", "sw")
			System.setProperty("prism.text", "t2k")
		} else {
			log.info ">>> LIVE MODE"
		}
	}

	@Override
	void init() throws Exception {
		stage = FxToolkit.registerStage {
			new Stage()
		}
		stage.setTitle(APP_SPEC_WIDOW_TITLE)

		AppPrefs.fix(prefs)
		prefs.put(PrefKeys._version, AppPrefs.currentVersion.name())
		prefs.put(PrefKeys.wasDisclaimerAccepted, "true")
		overrideAppPrefs()
	}

	void overrideAppPrefs() {}

	def withCurrentProject(final fileName = 'project1') {
		def pPath = tempDir.resolve(fileName)
		appController.setCurrentProject(pPath.toFile())
		return pPath
	}

	@Override
	public void start(Stage stage) throws Exception {
		final FXMLLoader fxmlLoader = abakusfx.ResourceLoader.loader.getFxmlLoader("app.fxml")
		root = fxmlLoader.load()
		appController = (AppController) fxmlLoader.getController()

		Scene scene = new Scene(root)
		stage.setScene(scene)
		stage.show()

		projectTabsPane = lookup("#tabPane").query()
		projectTabs = projectTabsPane.getTabs()
	}

	/**
	 * from https://github.com/TestFX/TestFX/issues/634
	 * 
	 * @param idx the zero-based index of the tab to be selected
	 * @return the selected project tab - actually only the skin class
	 */
	def queryNthTab(idx) {
		lookup(".tab-pane > .tab-header-area > .headers-region > .tab").nth(idx).query()
	}

	MenuItem menuItem(itemQuery) {
		lookup(itemQuery).query().getItem()
	}

	def click(query) {
		clickOn(lookup(query).query())
	}


	def clickRight(query) {
		clickOn(lookup(query).query(), MouseButton.SECONDARY)
	}

	@Override
	void stop() throws Exception {
		FxToolkit.hideStage()
	}
}