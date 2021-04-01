package abakus;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a series and projection of Stellen
 */
public class Anstellung {

	private final NavigableMap<YearMonth, Stelle> stelleByBeginn;
	final YearMonth ende;
	final BigDecimal agz;

	private Anstellung(final YearMonth ende, final BigDecimal agz) {
		this.stelleByBeginn = new TreeMap<>();
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

		assert !stelleByBeginn.containsKey(beginn);
		stelleByBeginn.put(beginn, antrittsStelle);
	}

	YearMonth getBeginn() {
		return stelleByBeginn.firstKey();
	}

	Stelle am(final YearMonth ym) {

		if (ym.isAfter(ende))
			throw Errors.illegalArg("Argument %s liegt nach dem Anstellungsende %s", ym, ende);

		final Entry<YearMonth, Stelle> entry = stelleByBeginn.floorEntry(ym);
		if (entry == null)
			throw Errors.illegalArg("Keine Stelle zu %s gefunden (frühest bekannte ist %s)", ym, getBeginn());

		final Stelle stelle = entry.getValue();
		final Stufe neueStufe = stelle.stufe.stufeAm(entry.getKey(), ym);
		return neueStufe == stelle.stufe ? stelle : Stelle.of(stelle, neueStufe);
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

	List<YearMonth> monthsInYear(final int year) {
		return IntStream.rangeClosed(1, 12).mapToObj(m -> YearMonth.of(year, m)).filter(this::isInAnstellung)
				.collect(Collectors.toList());
	}

	boolean isInAnstellung(final YearMonth ym) {
		return !ym.isBefore(getBeginn()) && !ym.isAfter(ende);
	}

	/**
	 * @return the Stellen of the argument year for the Sonderzahlung
	 */
	List<Stelle> calcBaseStellen(final int year) {

		final List<YearMonth> yms = Stream.of(Month.JULY, Month.AUGUST, Month.SEPTEMBER).map(m -> YearMonth.of(year, m))
				.filter(this::isInAnstellung).collect(Collectors.toList());
		if (yms.isEmpty())
			yms.add(YearMonth.of(year, getBeginn().getMonth().getValue()));
		return yms.stream().map(this::am).collect(Collectors.toList());
	}
}