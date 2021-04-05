package abakusfx;

import java.math.BigDecimal;
import java.time.YearMonth;

import org.javamoney.moneta.Money;

import abakus.Stelle;

class SeriesÜbersicht {
	final YearMonth von;
	final Stelle beginn;
	final YearMonth bis;
	final Stelle ende;

	final BigDecimal umfang;
	final BigDecimal agz;

	final Money summe;

	public SeriesÜbersicht(final YearMonth von, final Stelle beginn, final YearMonth bis, final Stelle ende,
			final BigDecimal umfang, final BigDecimal agz, final Money summe) {
		this.von = von;
		this.beginn = beginn;
		this.bis = bis;
		this.ende = ende;
		this.umfang = umfang;
		this.agz = agz;
		this.summe = summe;
	}
}