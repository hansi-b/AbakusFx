package abakus

import groovy.transform.Immutable
import org.javamoney.moneta.Money

import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

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
    static final BigDecimal zuschlagProzent = Constants.percent(130)

    private final Tarif tarif

    KostenRechner(Tarif tarif) {
        this.tarif = tarif
    }

    List<Monatskosten> monatsKosten(Stelle ausgangsStelle, LocalDate von, LocalDate bis) {
        if (bis < von)
            throw new IllegalArgumentException("Enddatum ${bis} liegt vor dem Anfang ${von}")
        if (von < ausgangsStelle.beginn)
            throw new IllegalArgumentException("Argument ${von} liegt vor dem Anfang ${ausgangsStelle.beginn}")

        def stichtag = von.withDayOfMonth(von.lengthOfMonth())
        def ende = bis.withDayOfMonth(bis.lengthOfMonth())

        List<Monatskosten> kostenListe = []
        while (stichtag <= ende) {
            def aktStelle = ausgangsStelle.am(stichtag)
            def brutto = monatsBrutto(aktStelle.gruppe, aktStelle.stufe, stichtag.year, aktStelle.umfang)
            sonderzahlung(stichtag, bis, ausgangsStelle)
            kostenListe << new Monatskosten(stichtag: stichtag, stelle: aktStelle, brutto: brutto, sonderzahlung: Constants.euros(0))
            stichtag = nextStichtag(stichtag)
        }

        return kostenListe
    }

    static LocalDate nextStichtag(LocalDate current) {
        def next = current.plusMonths(1)
        next.withDayOfMonth(next.lengthOfMonth())
    }

    Money monatsBrutto(Gruppe gruppe, Stufe stufe, int jahr, BigDecimal umfang) {
        tarif.brutto(gruppe, stufe, jahr) * zuschlagProzent * Constants.percent(umfang)
    }

    /**
     * Calculate the Jahressonderzahlung according to
     * https://oeffentlicher-dienst.info/tv-l/allg/jahressonderzahlung.html
     */
    Money sonderzahlung(LocalDate stichtag, LocalDate bis, Stelle ausgangsStelle) {

        // 1. only in November
        if (stichtag.month != Month.NOVEMBER)
            return Constants.euros(0)

        def year = stichtag.year
        // 2. only if to be employed at least for the coming December
        if (bis.isBefore(LocalDate.of(year, 12, 1)))
            return Constants.euros(0)

        def baseStellen = calcBaseStellen(year, ausgangsStelle)
        def kosten = baseStellen.collect { monatsBrutto(it.gruppe, it.stufe, year, it.umfang) }
        def summe = Constants.euros(0)
        kosten.each {summe = summe.add(it) }
        return summe.divide(kosten.size())
    }

    def calcBaseStellen(int year, Stelle ausgangsStelle) {

        assert ausgangsStelle.beginn.isBefore(YearMonth.of(year, Month.NOVEMBER).atEndOfMonth())

        def months = [Month.JULY, Month.AUGUST, Month.SEPTEMBER]
                .collect { YearMonth.of(year, it).atEndOfMonth() }
                .findAll { it.isAfter(ausgangsStelle.beginn) }
                ?: [YearMonth.of(year, ausgangsStelle.beginn.month).atEndOfMonth()]
        return months.collect { ausgangsStelle.am(it) }
    }
}
