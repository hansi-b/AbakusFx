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
package abakusfx.models;

import static abakus.Constants.eq;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonModel {
	public final String name;
	public final SeriesModel series;

	public PersonModel(@JsonProperty("name") final String name, @JsonProperty("series") final SeriesModel series) {
		this.name = name;
		this.series = series;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, series);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		final PersonModel other = (PersonModel) obj;
		return eq(name, other.name) && eq(series, other.series);
	}
}