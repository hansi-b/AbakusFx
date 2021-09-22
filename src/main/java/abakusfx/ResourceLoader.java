/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2021  Hans Bering
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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