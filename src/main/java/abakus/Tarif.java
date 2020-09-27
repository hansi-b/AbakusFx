package abakus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.javamoney.moneta.Money;

class Gehälter {
	final BigDecimal sonderzahlungProzent;
	final Map<Stufe, Money> bruttos;

	Gehälter(final BigDecimal szProzent, final Map<Stufe, Money> bruttos) {
		this.sonderzahlungProzent = szProzent;
		this.bruttos = bruttos;
	}
}

public class Tarif {

	private final Map<Gruppe, Map<Integer, Gehälter>> gehälter;

	public Tarif(final Map<Gruppe, Map<Integer, Gehälter>> parseGehälter) {
		this.gehälter = parseGehälter;
	}

	/**
	 * @return the 100%-Bruttogehalt for the given Gruppe, Stufe, and Year
	 */
	Money brutto(final Gruppe gruppe, final Stufe stufe, final int jahr) {
		return lookupYearTolerant(gruppe, jahr).bruttos.get(stufe);
	}

	/**
	 * @return the Sonderzahlung on a 100%-Bruttogehalt for the given Gruppe, Stufe,
	 *         and Year
	 */
	Money sonderzahlung(final Gruppe gruppe, final Stufe stufe, final int jahr) {
		final Gehälter geh = lookupYearTolerant(gruppe, jahr);
		return geh.bruttos.get(stufe).multiply(geh.sonderzahlungProzent);
	}

	private Gehälter lookupYearTolerant(final Gruppe gruppe, final int jahr) {
		final Map<Integer, Gehälter> gruppenGehälter = gehälter.get(gruppe);
		final Set<Integer> jahre = gruppenGehälter.keySet();

		if (jahre.contains(jahr))
			return gruppenGehälter.get(jahr);

		final Integer maxJahr = Collections.max(jahre);
		if (jahr > maxJahr)
			return gruppenGehälter.get(maxJahr);

		throw new IllegalArgumentException("Keine Gehälter für das Jahr ${jahr} (frühestes ist ${jahre.min()})");
	}
}
