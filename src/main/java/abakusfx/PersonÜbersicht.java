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

import java.util.List;
import java.util.Objects;

import org.javamoney.moneta.Money;

import abakus.Constants;

class PersonÜbersicht {
	final String name;
	final SeriesÜbersicht serie;

	PersonÜbersicht(final String name, final SeriesÜbersicht serie) {
		this.name = name;
		this.serie = serie;
	}

	static Money sumÜbersichten(final List<PersonÜbersicht> übersichten) {
		return übersichten.stream().map(p -> p.serie.summe).filter(Objects::nonNull).reduce(Money::add)
				.orElseGet(() -> Constants.euros(0));
	}
}