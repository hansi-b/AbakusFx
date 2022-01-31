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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import abakus.Gruppe;
import abakus.Stufe;

public class SeriesModel {

	private static final BigDecimal defaultAgz = BigDecimal.valueOf(30);

	public final LocalDate von;
	public final LocalDate bis;
	public final Gruppe gruppe;
	public final Stufe stufe;
	public final int umfang;
	public final boolean isWeiter;
	public final LocalDate seit;
	public final int umfangSeit;
	public final BigDecimal agz;

	@JsonCreator
	public SeriesModel(@JsonProperty("von") final LocalDate von, @JsonProperty("bis") final LocalDate bis,
			@JsonProperty("gruppe") final Gruppe gruppe, @JsonProperty("stufe") final Stufe stufe,
			@JsonProperty("umfang") final int umfang, @JsonProperty("isWeiter") final boolean isWeiter,
			@JsonProperty("seit") final LocalDate seit, @JsonProperty("umfangSeit") final int umfangSeit,
			@JsonProperty("agz") final BigDecimal agz) {

		this.von = von;
		this.bis = bis;
		this.gruppe = gruppe;
		this.stufe = stufe;
		this.umfang = umfang;
		this.isWeiter = isWeiter;
		this.seit = seit;
		this.umfangSeit = umfangSeit;
		this.agz = agz == null ? defaultAgz : agz;
	}

	@Override
	public int hashCode() {
		return Objects.hash(von, bis, gruppe, stufe, umfang, isWeiter, seit, umfangSeit, agz);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		final SeriesModel other = (SeriesModel) obj;

		return eq(von, other.von) && eq(bis, other.bis) && //
				gruppe == other.gruppe && stufe == other.stufe && //
				eq(umfang, other.umfang) && isWeiter == other.isWeiter && //
				eq(seit, other.seit) && eq(umfangSeit, other.umfangSeit) && eq(agz, other.agz);
	}

	public static SeriesModel fallback() {
		return new SeriesModel(LocalDate.now(), LocalDate.now().plusMonths(12), Gruppe.E10, Stufe.eins, 100, false,
				LocalDate.now().minusMonths(6), 100, defaultAgz);
	}
}
