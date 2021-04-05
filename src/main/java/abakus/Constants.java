package abakus;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;

public class Constants {

	public static final Locale locale = Locale.GERMANY;
	private static final CurrencyUnit eur = Monetary.getCurrency(locale);

	public static final NumberFormat getNumberFormat() {
		return NumberFormat.getInstance(locale);
	}

	private static final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
	static {
		df.setParseBigDecimal(true);
	}

	static BigDecimal toBigDec(final String floatStr) {
		try {
			return (BigDecimal) df.parse(floatStr);
		} catch (final ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	static Money toEuro(final String floatStr) {
		return Money.of(toBigDec(floatStr), eur);
	}

	public static Money euros(final Number amount) {
		return Money.of(amount, eur);
	}

	static Money eurosRounded(final Number amount) {
		return eurosRounded(euros(amount));
	}

	static Money eurosRounded(final Money amount) {
		return amount.with(Monetary.getDefaultRounding());
	}

	static BigDecimal percent(final BigDecimal percent) {
		return percent.divide(BigDecimal.valueOf(100));
	}

	static BigDecimal percent(final int percent) {
		return percent(BigDecimal.valueOf(percent));
	}

	public static <T> boolean eq(final T one, final T other) {
		return one == null ? other == null : one.equals(other);
	}
}