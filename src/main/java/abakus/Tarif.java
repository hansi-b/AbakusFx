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
import java.time.YearMonth;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.javamoney.moneta.Money;

class Gehälter {

	/**
	 * A comparator which only tests for equality with respect to year and Gruppe.
	 * Use with caution: Not consistent with a general notion of equals for
	 * Gehälter!
	 */
	static final Comparator<Gehälter> gültigkeitUndGruppeComparator = (o1, o2) -> {
		final int gCmp = o1.gruppe.compareTo(o2.gruppe);
		return gCmp != 0 ? gCmp : o1.gültigAb.compareTo(o2.gültigAb);
	};

	final Gruppe gruppe;
	final YearMonth gültigAb;
	final BigDecimal sonderzahlungProzent;
	final EnumMap<Stufe, Money> bruttos;

	Gehälter(final Gruppe gruppe, final YearMonth gültigAb, final BigDecimal szProzent,
			final EnumMap<Stufe, Money> bruttos) {
		this.gruppe = gruppe;
		this.gültigAb = gültigAb;
		this.sonderzahlungProzent = szProzent;
		this.bruttos = bruttos;
	}
}

public class Tarif {

	private final EnumMap<Gruppe, NavigableMap<YearMonth, Gehälter>> gehälterMapping;

	Tarif(final Set<Gehälter> parsedGehälter) {
		this.gehälterMapping = new EnumMap<>(Gruppe.class);
		parsedGehälter
				.forEach(g -> this.gehälterMapping.computeIfAbsent(g.gruppe, k -> new TreeMap<>()).put(g.gültigAb, g));
	}

	/**
	 * @return the 100%-Bruttogehalt for the given Gruppe, Stufe, and YearMonth
	 */
	ExplainedMoney brutto(final Gruppe gruppe, final Stufe stufe, final YearMonth ym) {
		return explainedBrutto(lookupYearTolerant(gruppe, ym), stufe);
	}

	/**
	 * @return the Sonderzahlung on a 100%-Bruttogehalt for the given Gruppe, Stufe,
	 *         and Year
	 */
	ExplainedMoney sonderzahlung(final Gruppe gruppe, final Stufe stufe, final int jahr) {
		final Gehälter geh = lookupYearTolerant(gruppe, YearMonth.of(jahr, 11));
		return explainedBrutto(geh, stufe).multiplyPercent(geh.sonderzahlungProzent, "JSZ");
	}

	private ExplainedMoney explainedBrutto(final Gehälter gehälter, final Stufe stufe) {
		return ExplainedMoney.of(gehälter.bruttos.get(stufe),
				String.format("TV-L %s/%s ab %s", gehälter.gruppe, stufe.asString(), gehälter.gültigAb));
	}

	private Gehälter lookupYearTolerant(final Gruppe gruppe, final YearMonth ym) {
		final NavigableMap<YearMonth, Gehälter> gruppenGehälter = gehälterMapping.get(gruppe);
		final Entry<YearMonth, Gehälter> floor = gruppenGehälter.floorEntry(ym);

		if (floor == null)
			throw new IllegalArgumentException(String.format("Keine Tarifdaten für %s vorhanden (frühestes ist %s", ym,
					gruppenGehälter.firstKey()));
		return floor.getValue();
	}
}
