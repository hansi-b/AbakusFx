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

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.YearMonth;

import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;

import abakus.Constants;
import abakus.Stufe;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class Converters {

	public static final StufeConverter stufeConverter = new StufeConverter();

	// used in FXML
	public static StufeConverter createStufeConverter() {
		return stufeConverter;
	}

	public static class StufeConverter extends StringConverter<Stufe> {

		@Override
		public String toString(final Stufe stufe) {
			return stufe.asString();
		}

		@Override
		public Stufe fromString(final String string) {
			return Stufe.fromString(string);
		}
	}

	public static final YearMonthConverter yearMonthConverter = new YearMonthConverter();

	public static class YearMonthConverter extends StringConverter<YearMonth> {

		@Override
		public String toString(final YearMonth yearMonth) {
			return yearMonth.toString();
		}

		@Override
		public YearMonth fromString(final String string) {
			return YearMonth.parse(string);
		}
	}

	public static final MoneyConverter moneyConverter = new MoneyConverter();

	public static class MoneyConverter extends StringConverter<Money> {
		private static final MonetaryAmountFormat format = MonetaryFormats
				.getAmountFormat(AmountFormatQueryBuilder.of(Constants.locale).set(CurrencyStyle.SYMBOL).build());

		@Override
		public String toString(final Money money) {
			return format.format(money);
		}

		@Override
		public Money fromString(final String string) {
			return Money.parse(string);
		}
	}

	public static class UmfangSpinnerFactory extends SpinnerValueFactory.IntegerSpinnerValueFactory {

		private static class UmfangConverter extends IntegerStringConverter {

			@Override
			public String toString(final Integer percent) {
				return String.format("%d%%", percent);
			}

			@Override
			public Integer fromString(final String string) {
				final String numStr = string.replace("%", "");

				return numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
			}
		}

		public UmfangSpinnerFactory() {
			super(0, 100, 100, 5);
			setConverter(new UmfangConverter());
		}
	}

	public static class AgzSpinnerFactory extends SpinnerValueFactory.DoubleSpinnerValueFactory {

		private static class AgzConverter extends DoubleStringConverter {
			private static final Logger log = LogManager.getLogger();
			private final NumberFormat nf = Constants.getNumberFormat();

			@Override
			public String toString(final Double percent) {
				return String.format("%.3f%%", percent);
			}

			@Override
			public Double fromString(final String string) {
				final String numStr = string.replace("%", "");

				try {
					return numStr.isEmpty() ? 0 : nf.parse(numStr).doubleValue();
				} catch (final ParseException e) {
					log.error("Could not parse double string value '%s'", string, e);
					return 0.;
				}
			}
		}

		public AgzSpinnerFactory() {
			super(0., 100., 30., 1.);
			setConverter(new AgzConverter());
		}
	}
}
