package abakusfx;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Supplier;

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

public class KostenTabController {
	private static final Logger log = LogManager.getLogger();

	@FXML
	private SerieSettingsController serieSettingsController;
	@FXML
	private SerieTableController serieTableController;

	private Supplier<KostenRechner> lazyRechner;

	private final ReadOnlyObjectWrapper<Money> summeInternalProperty = new ReadOnlyObjectWrapper<>(null);
	final ReadOnlyObjectProperty<Money> summeProperty = summeInternalProperty.getReadOnlyProperty();

	@FXML
	void initialize() {
		log.trace("KostenTabController.initialize");
		serieSettingsController.calcKosten.setOnAction(a -> updateResult());
		serieSettingsController.addDirtyListener(this::clearResult);
	}

	void setKostenRechner(final Supplier<KostenRechner> lazyRechner) {
		this.lazyRechner = lazyRechner;
	}

	void updateResult() {

		final List<Monatskosten> moKosten = calcMonatsKosten();
		serieTableController.updateKosten(moKosten);
		final Money summe = lazyRechner.get().summe(moKosten);
		summeInternalProperty.set(summe);
		log.trace("updateResult -> {}", summe);
	}

	private List<Monatskosten> calcMonatsKosten() {
		final YearMonth von = serieSettingsController.getVon();
		final YearMonth bis = serieSettingsController.getBis();
		return lazyRechner.get().monatsKosten(serieSettingsController.getAnstellung(), von, bis);
	}

	SeriesÜbersicht getÜbersicht() {
		final List<Monatskosten> moKosten = calcMonatsKosten();

		return new SeriesÜbersicht(serieSettingsController.getVon(), moKosten.get(0).stelle,
				serieSettingsController.getBis(), moKosten.get(moKosten.size() - 1).stelle,
				serieSettingsController.getUmfang(), serieSettingsController.getAgz(), summeInternalProperty.get());
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