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

import java.util.Objects;

import abakus.Gruppe;
import abakus.Stelle;
import abakus.Stufe;

class GruppeStufe implements Comparable<GruppeStufe> {
	final Gruppe gruppe;
	final Stufe stufe;

	private GruppeStufe(final Gruppe gruppe, final Stufe stufe) {
		this.gruppe = gruppe;
		this.stufe = stufe;
	}

	static GruppeStufe of(final Stelle stelle) {
		return stelle != null ? new GruppeStufe(stelle.gruppe(), stelle.stufe()) : null;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof GruppeStufe gs) && compareTo(gs) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(gruppe, stufe);
	}

	@Override
	public int compareTo(final GruppeStufe o) {
		final int gCmp = gruppe.compareTo(o.gruppe);
		return gCmp != 0 ? gCmp : stufe.compareTo(o.stufe);
	}

	@Override
	public String toString() {
		return String.format("%s/%s", gruppe, stufe.asString());
	}
}