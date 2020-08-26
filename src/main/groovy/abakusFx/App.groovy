package abakusFx

import abakus.Constants
import groovy.util.logging.Log4j2
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

import java.util.logging.Level
import java.util.logging.Logger

@Log4j2
class App extends Application {

    private AppController mainController

    @Override
    void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Constants.locale)

        def fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("app.fxml"))
        Parent root = fxmlLoader.load()
        mainController = fxmlLoader.getController()

        primaryStage.setTitle("Abakus")
        primaryStage.setScene(new Scene(root))
        primaryStage.show()
    }

    static void main(String[] args) {
        Logger logger = Logger.getLogger("org.javamoney.moneta")
        logger.setLevel(Level.WARNING)

        launch(App, args)
    }

    @Override
    void stop() {
        mainController.stop()
    }
}
