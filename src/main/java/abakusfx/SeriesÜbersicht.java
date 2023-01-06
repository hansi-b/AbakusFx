/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2023 Hans Bering
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