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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.javamoney.moneta.Money;

public class ÖtvCsvParser {

	private static final String ötvCsv = "ötv.csv";

	public Tarif parseTarif() throws IOException {
		final InputStream resStream = getClass().getClassLoader().getResourceAsStream(ötvCsv);
		return new Tarif(parseGehälter(resStream));
	}

	Set<Gehälter> parseGehälter(final InputStream gehälterCsv) throws IOException {

		final Set<Gehälter> result = new TreeSet<>(Gehälter.jahrUndGruppeComparator);
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(gehälterCsv))) {
			br.lines().map(String::trim).filter((final String l) -> !l.isEmpty() && !l.startsWith("#"))
					.forEach(lStr -> {
						final Gehälter gehälter = parseGehälter(lStr);
						if (result.contains(gehälter))
							throw Errors.illegalArg("Doppelte Daten für Gruppe %s im Jahr %d", gehälter.gruppe,
									gehälter.jahr);
						result.add(gehälter);
					});
		}
		return result;
	}

	private static Gehälter parseGehälter(final String csvLine) {
		final String[] parts = csvLine.split("\t");
		if (parts.length != 9)
			throw Errors.illegalArg("Zeile enthält %d Feld(er) (nicht 9): '%s'", parts.length, Arrays.toString(parts));
		final Integer jahr = Integer.valueOf(parts[0]);
		final Gruppe gruppe = Gruppe.valueOf(parts[1]);

		final BigDecimal sz = Constants.toBigDec(parts[2]);
		final Map<Stufe, Money> bruttoByStufe = new EnumMap<>(Stufe.class);
		for (int p = 3; p < parts.length; p++)
			bruttoByStufe.put(Stufe.values()[p - 3], Constants.toEuro(parts[p]));

		return new Gehälter(gruppe, jahr, sz, bruttoByStufe);
	}
}
