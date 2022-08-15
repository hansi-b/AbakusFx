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

	private Constants() {
		// instantiation prevention
	}

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
}