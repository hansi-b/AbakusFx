package abakus

import groovy.transform.Immutable
import org.javamoney.moneta.Money

import java.time.Month
import java.time.YearMonth

import static abakus.Constants.euros
import static abakus.Constants.percent

@Immutable(knownImmutableClasses = [Money])
class Monatskosten {
    YearMonth stichtag
    Stelle stelle
    Money brutto
    Money sonderzahlung
}

class KostenRechner {

    /**
     * fixer Prozentsatz, der als Arbeitgeber-Kostenzuschlag genommen wird
     */
    static final BigDecimal zuschlagProzent = percent(130)

    private final Tarif tarif

    KostenRechner(Tarif tarif) {
        this.tarif = tarif
    }

    /**
     * @param anst die zugrundeliegende Anstellung
     * @param von der Startmonat (inklusiv)
     * @param bis der Endmonat (inklusiv)
     * @return a list of the Monatskosten
     */
    List<Monatskosten> monatsKosten(Anstellung anstellung, YearMonth von, YearMonth bis) {
        if (bis < von)
            throw new IllegalArgumentException("Enddatum ${bis} liegt vor dem Anfang ${von}")

        anstellung.monatsStellen(von, bis).collect {
            def ym = it.key
            def aktStelle = it.value
            def money = monatsBrutto(aktStelle.gruppe, aktStelle.stufe, ym.year, aktStelle.umfang)
            def sz = euros(0) // sonderzahlung(current, bis, ausgangsStelle)
            new Monatskosten(stichtag: ym, stelle: aktStelle, brutto: money, sonderzahlung: sz)
        }
    }

    Money monatsBrutto(Gruppe gruppe, Stufe stufe, int jahr, BigDecimal umfang) {
        tarif.brutto(gruppe, stufe, jahr) * zuschlagProzent * percent(umfang)
    }

    /**
     * Calculate the Jahressonderzahlung according to
     * https://oeffentlicher-dienst.info/tv-l/allg/jahressonderzahlung.html
     */
    Money sonderzahlung(YearMonth stichtag, Anstellung anstellung) {

        // 1. only in November
        if (stichtag.month != Month.NOVEMBER)
            return euros(0)

        def year = stichtag.year
        // 2. only if to be employed at least for the coming December
        if (anstellung.ende < YearMonth.of(year, 12))
            return euros(0)

        def baseStellen = anstellung.calcBaseStellen(year)
        def kosten = baseStellen.collect {
            tarif.sonderzahlung(it.gruppe, it.stufe, year) * zuschlagProzent * percent(it.umfang)
        }
        def summe = euros(0)
        kosten.each { summe = summe.add(it) }
        return summe.divide(kosten.size())
    }
}
