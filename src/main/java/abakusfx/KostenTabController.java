package abakusfx;

import java.time.YearMonth;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;

import abakus.KostenRechner;
import abakus.Monatskosten;
import abakusfx.models.SeriesModel;
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

	private ReadOnlyObjectProperty<KostenRechner> kostenRechnerGetter;

	private final ReadOnlyObjectWrapper<Money> summeInternalProperty = new ReadOnlyObjectWrapper<>(null);
	final ReadOnlyObjectProperty<Money> summeProperty = summeInternalProperty.getReadOnlyProperty();

	@FXML
	void initialize() {
		log.trace("KostenTabController.initialize");
		calcKosten.setOnAction(a -> updateResult());
		serieSettingsController.addDirtyListener(this::clearResult);
	}

	void setKostenRechner(final ReadOnlyObjectProperty<KostenRechner> kostenRechnerGetter) {
		this.kostenRechnerGetter = kostenRechnerGetter;
	}

	void updateResult() {

		final YearMonth von = serieSettingsController.getVon();
		final YearMonth bis = serieSettingsController.getBis();

		final KostenRechner rechner = kostenRechnerGetter.get();
		final List<Monatskosten> moKosten = rechner.monatsKosten(serieSettingsController.getAnstellung(), von, bis);
		serieTableController.updateKosten(moKosten);
		final Money summe = rechner.summe(moKosten);
		summeInternalProperty.set(summe);
		log.trace("updateResult -> {}", summe);
	}

	private void clearResult() {
		if (summeInternalProperty.get() == null)
			return;
		summeInternalProperty.set(null);
		serieTableController.clearKosten();
		log.debug("Cleared result");
	}

	void addDirtyListener(final Runnable listener) {
		serieSettingsController.addDirtyListener(listener);
	}

	SeriesModel getState() {
		return serieSettingsController.getState();
	}

	void setState(final SeriesModel model) {
		serieSettingsController.setState(model);
	}

	@FXML
	void exit(final ActionEvent actionEvent) {
		log.trace("#exit on {}", actionEvent);
		Platform.exit();
	}
}