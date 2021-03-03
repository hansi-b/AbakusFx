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
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.KeyCode
import javafx.stage.Stage

/**
 * Basis boiler-plate class to derive FX-test-classes from.
 * Provides fields like the project tabs, and utility accessors.
 */
public class AbstractAbakusSpec extends ApplicationSpec {

	Parent root
	TabPane tabPane
	ObservableList<Tab> tabs

	@Override
	void init() throws Exception {
		FxToolkit.registerStage { new Stage() }
	}

	@Override
	public void start(Stage stage) throws Exception {
		final FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource("app.fxml"))
		root = fxmlLoader.load()

		Scene scene = new Scene(root)
		stage.setScene(scene)
		stage.show()

		tabPane = lookup("#tabPane").query()
		tabs = tabPane.getTabs()
	}

	/**
	 *
	 * @param idx the zero-based index of the tab to be selected
	 * @return the selected project tab
	 */
	def queryNthTab( idx) {
		lookup(".tab-pane > .tab-header-area > .headers-region > .tab").nth(idx).query()
	}

	MenuItem getItemFromMenu(menuQuery, itemQuery) {
		clickOn(lookup(menuQuery).query())
		lookup(itemQuery).query().getItem()
	}


	@Override
	void stop() throws Exception {
		FxToolkit.hideStage()
	}
}