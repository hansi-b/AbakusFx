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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import abakus.Anstellung;
import abakus.Gruppe;
import abakus.Stelle;
import abakus.Stufe;
import abakusfx.models.SeriesModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleGroup;

public class SerieSettingsController {
	@FXML
	DatePicker von;
	@FXML
	DatePicker bis;

	@FXML
	ComboBox<Gruppe> gruppe;

	@FXML
	Spinner<Integer> umfang;

	@FXML
	ComboBox<Stufe> stufe;

	@FXML
	ToggleGroup neuOderWeiter;
	@FXML
	RadioButton weiter;

	@FXML
	Label seitLabel;
	@FXML
	DatePicker seit;

	@FXML
	Label umfangSeitLabel;
	@FXML
	Spinner<Integer> umfangSeit;

	@FXML
	Spinner<Double> agz;

	@FXML
	Button calcKosten;

	@FXML
	void initialize() {
		von.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.getDayOfMonth() != 1)
				von.valueProperty().setValue(newValue.withDayOfMonth(1));
		});
		von.setDayCellFactory(datePicker -> new DateCell() {
			@Override
			public void updateItem(final LocalDate item, final boolean empty) {
				super.updateItem(item, empty);
				if (item.isAfter(bis.valueProperty().get())) {
					setDisable(true);
					setStyle("-fx-background-color: #ffc0cb;");
				}
			}
		});

		bis.valueProperty().addListener((observable, oldValue, newValue) -> {
			final int lastDay = newValue.lengthOfMonth();
			if (newValue.getDayOfMonth() != lastDay)
				bis.valueProperty().setValue(newValue.withDayOfMonth(lastDay));
		});
		bis.setDayCellFactory(datePicker -> new DateCell() {
			@Override
			public void updateItem(final LocalDate item, final boolean empty) {
				super.updateItem(item, empty);
				if (item.isBefore(von.valueProperty().get())) {
					setDisable(true);
					setStyle("-fx-background-color: #ffc0cb;");
				}
			}
		});

		gruppe.getItems().setAll(Gruppe.values());
		// set the first toggle true to have one true
		neuOderWeiter.getToggles().get(0).setSelected(true);
		stufe.getItems().setAll(Stufe.values());

		seitLabel.disableProperty().bind(weiter.selectedProperty().not());
		seit.disableProperty().bind(weiter.selectedProperty().not());
		seit.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.getDayOfMonth() != 1)
				seit.valueProperty().setValue(newValue.withDayOfMonth(1));
		});

		umfangSeitLabel.disableProperty().bind(weiter.selectedProperty().not());
		umfangSeit.disableProperty().bind(weiter.selectedProperty().not());

		setState(SeriesModel.fallback());
	}

	void setState(final SeriesModel model) {
		von.setValue(model.von);
		bis.setValue(model.bis);
		gruppe.setValue(model.gruppe);
		stufe.setValue(model.stufe);
		umfang.getValueFactory().setValue(model.umfang);
		weiter.setSelected(model.isWeiter);
		seit.setValue(model.seit);
		umfangSeit.getValueFactory().setValue(model.umfangSeit);
		agz.getValueFactory().setValue(model.agz.doubleValue());
	}

	SeriesModel getState() {
		return new SeriesModel(von.getValue(), bis.getValue(), gruppe.getValue(), stufe.getValue(), umfang.getValue(),
				weiter.isSelected(), seit.getValue(), umfangSeit.getValue(), getAgz());
	}

	void addDirtyListener(final Runnable dirtyListener) {
		von.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		bis.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		gruppe.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		stufe.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		umfang.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		seit.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		weiter.selectedProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		umfangSeit.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
		agz.valueProperty().addListener((ob, ov, nv) -> dirtyListener.run());
	}

	YearMonth getVon() {
		return YearMonth.from(von.getValue());
	}

	YearMonth getBis() {
		return YearMonth.from(bis.getValue());
	}

	BigDecimal getUmfang() {
		return BigDecimal.valueOf(umfang.getValue());
	}

	BigDecimal getAgz() {
		return BigDecimal.valueOf(agz.getValue());
	}

	Anstellung getAnstellung() {
		final boolean istWeiterBeschäftigung = weiter.selectedProperty().get();

		final Stelle stelle = Stelle.of(gruppe.getValue(), stufe.getValue(),
				istWeiterBeschäftigung ? umfangSeit.getValue() : umfang.getValue());

		return istWeiterBeschäftigung
				? Anstellung.weiter(YearMonth.from(seit.getValue()), stelle, getVon(), umfang.getValue(), getBis(),
						getAgz())
				: Anstellung.of(getVon(), stelle, getBis(), getAgz());
	}
}
