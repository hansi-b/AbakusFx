package abakusFx

import abakus.Constants
import groovy.util.logging.Log4j2
import javafx.application.Application
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

import java.util.logging.Level
import java.util.logging.Logger

@Log4j2
class App extends Application {

    private AppController mainController

    @Override
    void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Constants.locale)

        def stream = getClass().getClassLoader().getResourceAsStream("logo.png")
        if (!stream)
            log.warn "Could not load application icon"
        else
            primaryStage.getIcons().add(new Image(stream))

        def fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("app.fxml"))
        Parent root = fxmlLoader.load()
        mainController = fxmlLoader.getController()

        primaryStage.setTitle("Abakus")
        primaryStage.setScene(new Scene(root))
        primaryStage.show()

        mainController.fill(new AppTitle(primaryStage))
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

class AppTitle {
    private final Stage stage

    SimpleStringProperty projectName
    BooleanProperty isDirty

    AppTitle(Stage stage) {
        this.stage = stage
        this.projectName = new SimpleStringProperty("")
        this.isDirty = new SimpleBooleanProperty(false)

        projectName.addListener(new ChangeListener<String>() {
            @Override
            void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateTitle()
            }
        })
        isDirty.addListener(new ChangeListener<Boolean>() {
            @Override
            void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                updateTitle()
            }
        })
    }

    private void updateTitle() {
        def pName = projectName.get()
        if (pName && pName.endsWith(".aba"))
            pName = pName.substring(0, pName.length() - 4)
        def projectPart = pName ? " [${pName}]" : ""
        def dirtyPart = isDirty.get() ? "*" : ""
        stage.setTitle("Abakus${projectPart}${dirtyPart}")
    }
}