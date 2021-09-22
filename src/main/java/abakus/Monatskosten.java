/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2021  Hans Bering
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
import java.util.Objects;

public class Monatskosten {

	public final YearMonth stichtag;
	public final Stelle stelle;
	public final ExplainedMoney kosten;

	Monatskosten(final YearMonth stichtag, final Stelle stelle, final ExplainedMoney kosten) {
		this.stichtag = stichtag;
		this.stelle = stelle;
		this.kosten = kosten;
	}

	@Override
	public int hashCode() {
		return Objects.hash(kosten, stelle, stichtag);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final Monatskosten other = (Monatskosten) obj;

		return Constants.eq(kosten, other.kosten) && //
				Constants.eq(stelle, other.stelle) && //
				Constants.eq(stichtag, other.stichtag);
	}
}