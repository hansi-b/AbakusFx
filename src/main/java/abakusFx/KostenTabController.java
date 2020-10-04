package abakusFx;

import java.io.File;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import abakus.KostenRechner;
import abakus.Monatskosten;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class KostenTabController {
	private static final Logger log = LogManager.getLogger();

	@FXML
	private SerieSettingsController serieSettingsController;
	@FXML
	private Button calcKosten;
	@FXML
	private SerieTableController serieTableController;

	private KostenRechner rechner;

	private final ReadOnlyObjectWrapper<Money> summeInternalProperty = new ReadOnlyObjectWrapper<>(null);
	public final ReadOnlyObjectProperty<Money> summeProperty = summeInternalProperty.getReadOnlyProperty();

	@FXML
	void initialize() throws IOException {

		calcKosten.setOnAction(a -> fillResult());
		serieSettingsController.addDirtyListener(() -> clearResult());
	}

	void setKostenRechner(final KostenRechner rechner) {
		this.rechner = rechner;
	}

	void fillResult() {

		final YearMonth von = serieSettingsController.getVon();
		final YearMonth bis = serieSettingsController.getBis();

		final List<Monatskosten> moKosten = rechner.monatsKosten(serieSettingsController.getAnstellung(), von, bis);
		serieTableController.updateKosten(moKosten);
		summeInternalProperty.set(rechner.summe(moKosten));
	}

	void clearResult() {
		serieTableController.clearKosten();
		summeInternalProperty.set(null);
	}

	public void addDirtyListener(final Runnable listener) {
		serieSettingsController.addDirtyListener(listener);
	}

	public void saveSeries(final File file) throws IOException {
		serieSettingsController.saveSeries(file);
	}

	public void loadSeries(final File projectFile) throws IOException {
		serieSettingsController.loadSeries(projectFile);
	}

	public void reset() {
		serieSettingsController.reset();
	}

	void stop() {
		serieSettingsController.stop();
	}

	@FXML
	void exit(final ActionEvent actionEvent) {
		log.trace("#exit on {}", actionEvent);
		Platform.exit();
	}
}