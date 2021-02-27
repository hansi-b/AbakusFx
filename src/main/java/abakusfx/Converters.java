package abakusfx;

import java.time.YearMonth;

import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;

import abakus.Constants;
import abakus.Stufe;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
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

	public static class PercentSpinnerFactory extends SpinnerValueFactory.IntegerSpinnerValueFactory {

		private static class PercentConverter extends IntegerStringConverter {

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

		public PercentSpinnerFactory() {
			super(0, 100, 100, 5);
			setConverter(new PercentConverter());
		}
	}
}
