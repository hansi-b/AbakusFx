package abakus;

import static abakus.Constants.euros;
import static abakus.Constants.percent;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import javax.money.Monetary;

import org.javamoney.moneta.Money;

public class KostenRechner {

	/**
	 * fixer Prozentsatz, der als Arbeitgeber-Kostenzuschlag genommen wird
	 */
	static final BigDecimal zuschlagProzent = percent(130);

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
			return new Monatskosten(current, aktStelle,
					monatsBrutto(aktStelle.gruppe, aktStelle.stufe, current.getYear(), aktStelle.umfang),
					sonderzahlung(current, anstellung));
		}).collect(Collectors.toList());
	}

	public Money summe(List<Monatskosten> moKosten) {
		return moKosten.stream().map(moKo -> moKo.brutto.add(moKo.sonderzahlung)).reduce(Constants.euros(0),
				Money::add);
	}

	Money monatsBrutto(final Gruppe gruppe, final Stufe stufe, final int jahr, final BigDecimal umfang) {
		return tarif.brutto(gruppe, stufe, jahr).multiply(zuschlagProzent).multiply(percent(umfang));
	}

	/**
	 * Calculate the Jahressonderzahlung according to
	 * https://oeffentlicher-dienst.info/tv-l/allg/jahressonderzahlung.html
	 */
	Money sonderzahlung(final YearMonth stichtag, final Anstellung anstellung) {

		// 1. only in November
		if (stichtag.getMonth() != Month.NOVEMBER)
			return euros(0);

		final int year = stichtag.getYear();
		// 2. only if to be employed at least for the coming December
		if (anstellung.ende.isBefore(YearMonth.of(year, 12)))
			return euros(0);

		final double anteilig = anteilig(anstellung.monthsInYear(year).size());

		final List<Stelle> baseStellen = anstellung.calcBaseStellen(year);
		final Money summe = baseStellen.stream().map(
				s -> tarif.sonderzahlung(s.gruppe, s.stufe, year).multiply(zuschlagProzent).multiply(percent(s.umfang)))
				.reduce(euros(0), (a, b) -> a.add(b));

		return summe.divide(baseStellen.size()).multiply(anteilig).with(Monetary.getDefaultRounding());
	}

	static double anteilig(final int monthsInYear) {
		return monthsInYear / 12.;
	}
}