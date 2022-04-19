package abakusfx

import java.nio.file.Path

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.hansib.sundries.prefs.store.InMemoryPrefsStore
import org.hansib.sundries.prefs.store.PrefsStore
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import abakusfx.AppPrefs.PrefKeys
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.MenuItem
import javafx.stage.Stage
import spock.lang.TempDir

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
	PrefsStore<PrefKeys> prefsStore = new InMemoryPrefsStore<PrefKeys>()

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

		AppPrefs.fix(prefsStore)
		prefsStore.put(PrefKeys._version, AppPrefs.currentVersion.name())
		prefsStore.put(PrefKeys.wasDisclaimerAccepted, "true")
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

	@Override
	void stop() throws Exception {
		FxToolkit.hideStage()
	}
}