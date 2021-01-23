package abakus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.javamoney.moneta.Money;

public class ÖtvCsvParser {

	private static final String ötvCsv = "ötv.csv";

	public Tarif parseTarif() throws IOException {
		return new Tarif(parseGehälter());
	}

	private static class Line {
		final Gruppe gruppe;
		final int jahr;
		final Gehälter gehälter;

		private Line(final Gruppe gr, final int j, final Gehälter ge) {
			this.gruppe = gr;
			this.jahr = j;
			this.gehälter = ge;
		}
	}

	private Map<Gruppe, Map<Integer, Gehälter>> parseGehälter() throws IOException {
		final Map<Gruppe, Map<Integer, Gehälter>> gehälterMap = new EnumMap<>(Gruppe.class);
		try (final BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream(ötvCsv)))) {
			br.lines().map(String::trim).filter((final String l) -> !l.isEmpty() && !l.startsWith("#"))
					.forEach(lStr -> {
						final Line l = parseLine(lStr);
						final Gruppe gruppe = l.gruppe;
						final int jahr = l.jahr;
						final Gehälter gehälter = l.gehälter;

						final Map<Integer, Gehälter> gruppeMap = gehälterMap.computeIfAbsent(gruppe,
								g -> new HashMap<>());
						if (gruppeMap.containsKey(jahr))
							throw Errors.illegalArg("Doppelte Daten für Gruppe %s im Jahr %d", gruppe, jahr);
						gruppeMap.put(jahr, gehälter);
					});
		}
		return gehälterMap;
	}

	private static Line parseLine(final String csvLine) {
		final String[] parts = csvLine.split("\t");
		if (parts.length != 9)
			throw Errors.illegalArg("Zeile enthält %d Felder (nicht 9): '%s'", parts.length, Arrays.toString(parts));
		final Integer jahr = Integer.valueOf(parts[0]);
		final Gruppe gruppe = Gruppe.valueOf(parts[1]);

		final BigDecimal sz = Constants.percent(Constants.toBigDec(parts[2]));
		final Map<Stufe, Money> bruttoByStufe = new EnumMap<>(Stufe.class);
		for (int p = 3; p < parts.length; p++)
			bruttoByStufe.put(Stufe.values()[p - 3], Constants.toEuro(parts[p]));

		return new Line(gruppe, jahr, new Gehälter(sz, bruttoByStufe));
	}
}
