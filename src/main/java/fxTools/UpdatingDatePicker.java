package fxTools;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;

public class UpdatingDatePicker {

	private static class DateValidator {
		private static final String CSS_DATE_PICKER_ERROR = "date-picker-error";
		private final DatePicker datePicker;

		DateValidator(DatePicker datePicker) {
			this.datePicker = datePicker;
		}

		/**
		 * Validates a date picker's editor content; styles the date picker via CSS on
		 * error.
		 * 
		 * @return a validated date; or null, if the editor content is not a valid date
		 */
		LocalDate validateAndStyle() {
			LocalDate localDate = getValidatedLocalDate();

			final ObservableList<String> styleClass = datePicker.getEditor().getStyleClass();
			if (localDate != null)
				styleClass.remove(CSS_DATE_PICKER_ERROR);
			else
				styleClass.add(CSS_DATE_PICKER_ERROR);
			return localDate;
		}

		private LocalDate getValidatedLocalDate() {
			try {
				return datePicker.getConverter().fromString(datePicker.getEditor().getText());
			} catch (DateTimeParseException e) {
				return null;
			}
		}
	}

	public static void ensureDatePickerUpdate(DatePicker datePicker) {
		DateValidator validator = new DateValidator(datePicker);

		datePicker.getEditor().textProperty().addListener((obs, oVal, nVal) -> validator.validateAndStyle());
		datePicker.getEditor().focusedProperty().addListener((obj, wasFocused, isFocused) -> {
			if (isFocused)
				return;
			LocalDate date = validator.validateAndStyle();
			if (date != null)
				datePicker.setValue(date);
			else
				datePicker.getEditor().requestFocus();
		});
	}
}
