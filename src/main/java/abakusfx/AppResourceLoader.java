/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2022 Hans Bering
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hansib.sundries.Errors;
import org.hansib.sundries.ResourceLoader;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

class AppResourceLoader {

	private static final Logger log = LogManager.getLogger();

	private static final String ötvCsv = "ötv.csv";

	private ResourceLoader resourceLoader;

	AppResourceLoader() {
		this.resourceLoader = new ResourceLoader();
	}

	String loadDisclaimer() {
		try {
			return resourceLoader.getResourceAsString("disclaimer.txt");

		} catch (final RuntimeException | IOException e) {
			log.error("Could not load disclaimer", e);
			final Alert alert = new Alert(AlertType.ERROR,
					String.format("Die Nutzungsvereinbarung konnte nicht geladen werden: %s", e.getMessage()));
			alert.setTitle("Interner Fehler beim Laden der Nutzungsvereinbarung");
			alert.showAndWait();
			return null;
		}
	}

	InputStream getTarifStream() {
		return resourceLoader.getResourceStream(ötvCsv);
	}

	String getTarifString() {
		try {
			return resourceLoader.getResourceAsString(ötvCsv);
		} catch (IOException e) {
			throw Errors.illegalState(e, "Beim Auslesen der Tarifdaten aus '%s' ist ein Fehler aufgetreten", ötvCsv);
		}
	}

	String loadVersionProperties() {
		try {
			return resourceLoader.getResourceAsString("version.properties");
		} catch (IOException e) {
			log.error("Could not load version properties", e);
			return "Version unbekannt (Fehler beim Lesen)";
		}
	}

	String resourceAsString(final String resourceName) throws IOException {
		return resourceLoader.getResourceAsString(resourceName);
	}

	InputStream getResourceStream(String resourceName) {
		return resourceLoader.getResourceStream(resourceName);
	}

	FXMLLoader getFxmlLoader(String fxml) {
		return new FXMLLoader(getResourceUrl("fxml/" + fxml));
	}

	URL getResourceUrl(String resName) {
		return resourceLoader.getResourceUrl(resName);
	}
}