package abakusfx

import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.MouseButton
import javafx.stage.Stage

/**
 * Basis boiler-plate class to derive FX-test-classes from.
 * Provides fields like the project tabs, and utility accessors.
 */
public class AbstractAbakusSpec extends ApplicationSpec {

	Parent root
	Stage stage

	AppController appController
	AppPrefs appPrefs = Mock()

	TabPane projectTabsPane
	ObservableList<Tab> projectTabs

	def setupSpec() {
		if (Boolean.getBoolean("headless")) {
			println ">>> HEADLESS MODE"

			System.setProperty("testfx.robot", "glass");
			System.setProperty("testfx.headless", "true");
			System.setProperty("prism.order", "sw");
			System.setProperty("prism.text", "t2k");
		} else {
			println ">>> LIVE MODE"
		}
	}

	@Override
	void init() throws Exception {
		stage = FxToolkit.registerStage { new Stage() }
		AppPrefs.Factory.fixed(appPrefs)
		appPrefs.wasDisclaimerAccepted() >> true
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