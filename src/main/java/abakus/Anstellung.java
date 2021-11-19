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
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a series and projection of Stellen
 */
public class Anstellung {

	private final NavigableMap<YearMonth, Stelle> startByBeginn;
	final YearMonth ende;
	final BigDecimal agz;

	private Anstellung(final YearMonth ende, final BigDecimal agz) {
		this.startByBeginn = new TreeMap<>();
		this.ende = ende;
		this.agz = agz;
	}

	/**
	 * Eine Neuanstellung mit einer festen Stelle und einem gegebenen Ende (für die
	 * JSZ benötigt).
	 */
	public static Anstellung of(final YearMonth beginn, final Stelle antrittsStelle, final YearMonth ende,
			final BigDecimal agz) {
		final Anstellung a = new Anstellung(ende, agz);
		a.add(beginn, antrittsStelle);
		return a;
	}

	/**
	 * Eine Weiterbeschäftigung, evtl. mit verändertem Umfang. Stufe und Gruppe
	 * ergeben sich aus der Vorbeschäftigung.
	 */
	public static Anstellung weiter(final YearMonth vorigerBeginn, final Stelle vorigeStelle,
			final YearMonth neuerBeginn, final int neuerUmfang, final YearMonth ende, final BigDecimal agz) {

		final Anstellung a = new Anstellung(ende, agz);
		a.add(vorigerBeginn, vorigeStelle);
		a.add(neuerBeginn,
				Stelle.of(vorigeStelle.gruppe, vorigeStelle.stufe.stufeAm(vorigerBeginn, neuerBeginn), neuerUmfang));
		return a;
	}

	private void add(final YearMonth beginn, final Stelle antrittsStelle) {
		if (beginn.isAfter(ende))
			throw Errors.illegalArg("Stellenbeginn %s liegt nach dem Anstellungsende %s", beginn, ende);

		assert !startByBeginn.containsKey(beginn);
		startByBeginn.put(beginn, antrittsStelle);
	}

	NavigableMap<YearMonth, Stelle> monatsStellen(final YearMonth von, final YearMonth bis) {

		if (bis.isBefore(von))
			throw Errors.illegalArg("Enddatum %s liegt vor dem Anfang %s", bis, von);

		final NavigableMap<YearMonth, Stelle> stellenByYm = new TreeMap<>();
		YearMonth current = von;
		while (!current.isAfter(bis)) {
			stellenByYm.put(current, am(current));
			current = current.plusMonths(1);
		}

		return stellenByYm;
	}

	/**
	 * @return the Stellen of the argument year for the Sonderzahlung
	 */
	List<Stelle> calcBaseStellen(final int year) {

		List<YearMonth> yms = Stream.of(Month.JULY, Month.AUGUST, Month.SEPTEMBER).map(m -> YearMonth.of(year, m))
				.filter(this::isInAnstellung).toList();
		if (yms.isEmpty())
			yms = Collections.singletonList(YearMonth.of(year, getBeginn().getMonth().getValue()));
		return yms.stream().map(this::am).toList();
	}

	private Stelle am(final YearMonth ym) {

		if (ym.isAfter(ende))
			throw Errors.illegalArg("Argument %s liegt nach dem Anstellungsende %s", ym, ende);

		final Entry<YearMonth, Stelle> entry = startByBeginn.floorEntry(ym);
		if (entry == null)
			throw Errors.illegalArg("Keine Stelle zu %s gefunden (frühest bekannte ist %s)", ym, getBeginn());

		final Stelle stelle = entry.getValue();
		final YearMonth beginn = findStufenBeginn(entry);

		final Stufe neueStufe = stelle.stufe.stufeAm(beginn, ym);
		return neueStufe == stelle.stufe ? stelle : Stelle.of(stelle, neueStufe);
	}

	private YearMonth findStufenBeginn(final Entry<YearMonth, Stelle> entry) {
		final Stelle stelle = entry.getValue();
		YearMonth beginn = entry.getKey();

		Entry<YearMonth, Stelle> previous = startByBeginn.lowerEntry(beginn);
		while (previous != null && entry.getValue().gruppe == stelle.gruppe && entry.getValue().stufe == stelle.stufe) {
			beginn = previous.getKey();
			previous = startByBeginn.lowerEntry(beginn);
		}
		return beginn;
	}

	List<YearMonth> monthsInYear(final int year) {
		return IntStream.rangeClosed(1, 12).mapToObj(m -> YearMonth.of(year, m)).filter(this::isInAnstellung).toList();
	}

	private boolean isInAnstellung(final YearMonth ym) {
		return !ym.isBefore(getBeginn()) && !ym.isAfter(ende);
	}

	private YearMonth getBeginn() {
		return startByBeginn.firstKey();
	}
}