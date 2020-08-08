package abakus

import groovy.transform.Immutable
import org.javamoney.moneta.Money

import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

import static abakus.Constants.*

@Immutable(knownImmutableClasses = [Money])
class Monatskosten {
    LocalDate stichtag
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

    List<Monatskosten> monatsKosten(Anstellung anst, LocalDate von, LocalDate bis) {
        if (bis < von)
            throw new IllegalArgumentException("Enddatum ${bis} liegt vor dem Anfang ${von}")

        def stichtag = von.withDayOfMonth(von.lengthOfMonth())
        def ende = bis.withDayOfMonth(bis.lengthOfMonth())

        List<Monatskosten> kostenListe = []
        while (stichtag <= ende) {
            def aktStelle = anst.am(stichtag)
            def brutto = monatsBrutto(aktStelle.gruppe, aktStelle.stufe, stichtag.year, aktStelle.umfang)
            sonderzahlung(stichtag, bis, ausgangsStelle)
            kostenListe << new Monatskosten(stichtag: stichtag, stelle: aktStelle, brutto: brutto, sonderzahlung: euros(0))
            stichtag = nextStichtag(stichtag)
        }

        return kostenListe
    }

    static LocalDate nextStichtag(LocalDate current) {
        def next = current.plusMonths(1)
        endOfMonth(next.year, next.monthValue)
    }

    Money monatsBrutto(Gruppe gruppe, Stufe stufe, int jahr, BigDecimal umfang) {
        tarif.brutto(gruppe, stufe, jahr) * zuschlagProzent * percent(umfang)
    }

    /**
     * Calculate the Jahressonderzahlung according to
     * https://oeffentlicher-dienst.info/tv-l/allg/jahressonderzahlung.html
     */
    Money sonderzahlung(LocalDate stichtag, LocalDate bis, Stelle ausgangsStelle) {

        // 1. only in November
        if (stichtag.month != Month.NOVEMBER)
            return euros(0)

        def year = stichtag.year
        // 2. only if to be employed at least for the coming December
        if (bis.isBefore(startOfMonth(year, 12)))
            return euros(0)

        def baseStellen = calcBaseStellen(year, ausgangsStelle)
        def kosten = baseStellen.collect {
            tarif.sonderzahlung(it.gruppe, it.stufe, year) * zuschlagProzent * percent(it.umfang)
        }
        def summe = euros(0)
        kosten.each { summe = summe.add(it) }
        return summe.divide(kosten.size())
    }

    def static calcBaseStellen(int year, Stelle ausgangsStelle) {

        assert ausgangsStelle.beginn.isBefore(YearMonth.of(year, Month.NOVEMBER).atEndOfMonth())

        def months = [Month.JULY, Month.AUGUST, Month.SEPTEMBER]
                .collect { endOfMonth(year, it.value) }
                .findAll { it.isAfter(ausgangsStelle.beginn) }
                ?: [endOfMonth(year, ausgangsStelle.beginn.month.value)]
        return months.collect { ausgangsStelle.am(it) }
    }
}
