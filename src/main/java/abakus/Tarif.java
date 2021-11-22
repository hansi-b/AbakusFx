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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.javamoney.moneta.Money;

class Gehälter {

	/**
	 * A comparator which only tests for equality with respect to year and Gruppe.
	 * Use with caution: Not consistent with a general notion of equals for
	 * Gehälter!
	 */
	static final Comparator<Gehälter> jahrUndGruppeComparator = new Comparator<Gehälter>() {
		@Override
		public int compare(final Gehälter o1, final Gehälter o2) {
			final int gCmp = o1.gruppe.compareTo(o2.gruppe);
			return gCmp != 0 ? gCmp : Integer.compare(o1.jahr, o2.jahr);
		}
	};

	final Gruppe gruppe;
	final int jahr;
	final BigDecimal sonderzahlungProzent;
	final Map<Stufe, Money> bruttos;

	Gehälter(final Gruppe gruppe, final int jahr, final BigDecimal szProzent, final Map<Stufe, Money> bruttos) {
		this.gruppe = gruppe;
		this.jahr = jahr;
		this.sonderzahlungProzent = szProzent;
		this.bruttos = bruttos;
	}
}

public class Tarif {

	private final Map<Gruppe, Map<Integer, Gehälter>> gehälterMapping;

	Tarif(final Set<Gehälter> parsedGehälter) {
		this.gehälterMapping = new HashMap<>();
		parsedGehälter.forEach(g -> {
			this.gehälterMapping.computeIfAbsent(g.gruppe, k -> new HashMap<>()).put(g.jahr, g);
		});
	}

	/**
	 * @return the 100%-Bruttogehalt for the given Gruppe, Stufe, and Year
	 */
	ExplainedMoney brutto(final Gruppe gruppe, final Stufe stufe, final int jahr) {
		return explainedBrutto(lookupYearTolerant(gruppe, jahr), stufe);
	}

	/**
	 * @return the Sonderzahlung on a 100%-Bruttogehalt for the given Gruppe, Stufe,
	 *         and Year
	 */
	ExplainedMoney sonderzahlung(final Gruppe gruppe, final Stufe stufe, final int jahr) {
		final Gehälter geh = lookupYearTolerant(gruppe, jahr);
		return explainedBrutto(geh, stufe).multiplyPercent(geh.sonderzahlungProzent, "JSZ");
	}

	private ExplainedMoney explainedBrutto(final Gehälter gehälter, final Stufe stufe) {
		return ExplainedMoney.of(gehälter.bruttos.get(stufe),
				String.format("TV-L %d %s/%s", gehälter.jahr, gehälter.gruppe, stufe.asString()));
	}

	private Gehälter lookupYearTolerant(final Gruppe gruppe, final int jahr) {
		final Map<Integer, Gehälter> gruppenGehälter = gehälterMapping.get(gruppe);
		final Set<Integer> jahre = gruppenGehälter.keySet();

		if (jahre.contains(jahr))
			return gruppenGehälter.get(jahr);

		final Integer maxJahr = Collections.max(jahre);
		if (jahr > maxJahr)
			return gruppenGehälter.get(maxJahr);

		throw new IllegalArgumentException(String
				.format("Keine Tarifdaten für das Jahr %d vorhanden (frühestes ist %d)", jahr, Collections.min(jahre)));
	}
}
