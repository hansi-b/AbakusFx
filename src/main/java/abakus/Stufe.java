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

import java.time.YearMonth;

public enum Stufe {
	eins, zwei, drei, vier, fünf, sechs;

	public String asString() {
		return String.format("%d", ordinal() + 1);
	}

	public static Stufe fromString(final String ordStr) {
		return values()[Integer.valueOf(ordStr) - 1];
	}

	Stufe nächste() {
		return ordinal() < sechs.ordinal() ? values()[ordinal() + 1] : sechs;
	}

	YearMonth nächsterAufstieg(final YearMonth seit) {
		return seit.plusYears(ordinal() + 1L);
	}

	Stufe stufeAm(final YearMonth seit, final YearMonth am) {

		final Stufe n = nächste();
		if (n == this)
			return this;

		final YearMonth aufstieg = nächsterAufstieg(seit);
		return aufstieg.isAfter(am) ? this : n.stufeAm(aufstieg, am);
	}
}