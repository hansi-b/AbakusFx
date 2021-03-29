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
	static final BigDecimal zuschlagProzent = BigDecimal.valueOf(30);

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
			return new Monatskosten(current, aktStelle, monatsBrutto(aktStelle, current.getYear()),
					sonderzahlung(current, anstellung));
		}).collect(Collectors.toList());
	}

	public Money summe(final List<Monatskosten> moKosten) {
		return moKosten.stream().map(
				moKo -> moKo.brutto.money().add(moKo.sonderzahlung != null ? moKo.sonderzahlung.money() : euros(0)))
				.reduce(Constants.euros(0), Money::add);
	}

	ExplainedMoney monatsBrutto(final Stelle stelle, final int jahr) {
		final ExplainedMoney agzBrutto = tarif.brutto(stelle.gruppe, stelle.stufe, jahr).addPercent(zuschlagProzent,
				"AGZ");
		return stelle.istVollzeit() ? agzBrutto : agzBrutto.multiplyPercent(stelle.umfangPercent, "Umfang");
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
			return ExplainedMoney.of(euros(0), "JSZ appliziert nicht");

		final double anteilig = anteilig(anstellung.monthsInYear(year).size());

		final List<Stelle> baseStellen = anstellung.calcBaseStellen(year);
		final Money summe = baseStellen.stream()
				.map(s -> tarif.sonderzahlung(s.gruppe, s.stufe, year).money()
						.multiply(zuschlagProzent.add(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100)))
						.multiply(percent(s.umfangPercent)))
				.reduce(euros(0), Money::add);

		return ExplainedMoney
				.of(summe.divide(baseStellen.size()).multiply(anteilig).with(Monetary.getDefaultRounding()), "JSZ");
	}

	static double anteilig(final int monthsInYear) {
		return monthsInYear / 12.;
	}
}