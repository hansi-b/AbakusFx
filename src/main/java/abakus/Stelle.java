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
package abakus;

import java.math.BigDecimal;

public record Stelle(Gruppe gruppe, Stufe stufe, BigDecimal umfangPercent) {

	private static final BigDecimal BIG_100 = BigDecimal.valueOf(100);

	static Stelle of(final Gruppe g, final Stufe s) {
		return of(g, s, 100);
	}

	public static Stelle of(final Gruppe g, final Stufe s, final int umfang) {
		return of(g, s, BigDecimal.valueOf(umfang));
	}

	public static Stelle of(Gruppe g, Stufe s, BigDecimal umfang) {
		return new Stelle(g, s, umfang);
	}

	public boolean istVollzeit() {
		return BIG_100.compareTo(umfangPercent) == 0;
	}

	@Override
	public String toString() {
		return String.format("Stelle(%s/%s, %s%%)", gruppe, stufe, umfangPercent);
	}
}
