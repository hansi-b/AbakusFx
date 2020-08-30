package abakusFx

import abakus.Constants
import groovy.util.logging.Log4j2
import javafx.application.Application
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

@Log4j2
class AppTitle {
    private final Stage stage

    private String project
    private boolean isDirty

    AppTitle(Stage stage) {
        this.stage = stage
        this.project = null
        this.isDirty = false
    }

    void updateProject(String newProject) {
        log.debug "Updating title: project = '${newProject}'"
        this.project = newProject
        updateTitle()
    }

    void updateIsDirty(boolean newIsDirty) {
        log.debug "Updating title: isDirty = '${newIsDirty}'"
        this.isDirty = newIsDirty
        updateTitle()
    }

    private void updateTitle() {
        def pName = project
        if (pName && pName.endsWith(".aba"))
            pName = pName.substring(0, pName.length() - 4)
        def projectPart = pName ? " [${pName}]" : ""
        def dirtyPart = isDirty ? "*" : ""
        stage.setTitle("Abakus${projectPart}${dirtyPart}")
    }
}