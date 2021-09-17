package abakusfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class ResourceLoader {

	private static final Logger log = LogManager.getLogger();

	static final ResourceLoader loader = new ResourceLoader();

	String loadDisclaimer() {
		try {
			return ResourceLoader.loader.resourceAsString("disclaimer.txt");

		} catch (final RuntimeException | IOException e) {
			log.error("Could not load disclaimer", e);
			final Alert alert = new Alert(AlertType.ERROR,
					String.format("Die Nutzungsvereinbarung konnte nicht geladen werden: %s", e.getMessage()));
			alert.setTitle("Interner Fehler beim Laden der Nutzungsvereinbarung");
			alert.showAndWait();
			return null;
		}
	}

	String loadVersionProperties() {
		try {
			return ResourceLoader.loader.resourceAsString("version.properties");
		} catch (IOException e) {
			log.error("Could not load version properties", e);
			return "Version unbekannt (Fehler beim Lesen)";
		}
	}

	String resourceAsString(final String resourceName) throws IOException {
		try (InputStream resStream = getResourceStream(resourceName)) {
			return new String(resStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	InputStream getResourceStream(String resourceName) {
		return getClass().getClassLoader().getResourceAsStream(resourceName);
	}

	FXMLLoader getFxmlLoader(String fxml) {
		return new FXMLLoader(getClass().getClassLoader().getResource(fxml));
	}
}