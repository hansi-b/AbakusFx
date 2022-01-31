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

import static abakus.Constants.euros;
import static abakus.Constants.percent;

import java.time.Month;
import java.time.YearMonth;
import java.util.List;

import javax.money.Monetary;

import org.javamoney.moneta.Money;

public class KostenRechner {

	private final Tarif tarif;

	public KostenRechner(final Tarif tarif) {
		this.tarif = tarif;
	}

	/**
	 * @param anst die zugrundeliegende Anstellung
	 * @param von  der Startmonat (inklusiv)
	 * @param bis  der Endmonat (inklusiv)
	 * @return a list of the Monatskosten
	 */
	public List<Monatskosten> monatsKosten(final Anstellung anstellung, final YearMonth von, final YearMonth bis) {
		if (bis.isBefore(von))
			throw Errors.illegalArg("Enddatum %s liegt vor dem Anfang %s", bis, von);

		return anstellung.monatsStellen(von, bis).entrySet().stream().map(e -> {
			final YearMonth current = e.getKey();
			final Stelle aktStelle = e.getValue();
			final ExplainedMoney monatsBrutto = monatsBrutto(aktStelle, current);
			final ExplainedMoney sonderzahlung = sonderzahlung(current, anstellung);
			final ExplainedMoney summe = sonderzahlung != null ? monatsBrutto.add(sonderzahlung) : monatsBrutto;
			return new Monatskosten(current, aktStelle, summe.addPercent(anstellung.agz, "AGZ"));
		}).toList();
	}

	public Money summe(final List<Monatskosten> moKosten) {
		return moKosten.stream().map(moKo -> moKo.kosten.money()).reduce(Constants.euros(0), Money::add);
	}

	ExplainedMoney monatsBrutto(final Stelle stelle, final YearMonth ym) {
		final ExplainedMoney tarifBrutto = tarif.brutto(stelle.gruppe, stelle.stufe, ym);
		return stelle.istVollzeit() ? tarifBrutto : tarifBrutto.multiplyPercent(stelle.umfangPercent, "Umfang");
	}

	/**
	 * Calculate the Jahressonderzahlung according to
	 * https://oeffentlicher-dienst.info/tv-l/allg/jahressonderzahlung.html
	 *
	 * @return null if the Stichtag is not November; zero if the Stelle does not
	 *         last beyond November; the calculated JSZ otherwise
	 */
	ExplainedMoney sonderzahlung(final YearMonth stichtag, final Anstellung anstellung) {

		// 1. only in November
		if (stichtag.getMonth() != Month.NOVEMBER)
			return null;

		final int year = stichtag.getYear();
		// 2. only if to be employed at least for the coming December
		if (anstellung.ende.isBefore(YearMonth.of(year, 12)))
			return ExplainedMoney.of(euros(0), "keine JSZ");

		final List<Stelle> baseStellen = anstellung.calcBaseStellen(year);
		Money summe = baseStellen.stream()
				.map(s -> tarif.sonderzahlung(s.gruppe, s.stufe, year).money().multiply(percent(s.umfangPercent)))
				.reduce(euros(0), Money::add);
		summe = summe.divide(baseStellen.size());

		final int applicableMonths = anstellung.monthsInYear(year).size();
		if (applicableMonths != 12) {
			final double anteilig = applicableMonths / 12.;
			summe = summe.multiply(anteilig);
		}

		return ExplainedMoney.of(summe.with(Monetary.getDefaultRounding()), "JSZ");
	}
}