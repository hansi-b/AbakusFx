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

	public static StufeConverter createStufeConverter() {
		return new StufeConverter();
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
			private static final NumberFormat nf = NumberFormat.getInstance(Constants.locale);

			@Override
			public String toString(final Double percent) {
				return String.format("%.3f%%", percent);
			}

			@Override
			public Double fromString(final String string) {
				final String numStr = string.replace("%", "");

				try {
					return numStr.isEmpty() ? 0 : nf.parse(numStr).doubleValue();
				} catch (ParseException e) {
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
