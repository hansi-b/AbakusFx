package abakus

import groovy.transform.Immutable
import org.javamoney.moneta.Money

import java.time.LocalDate

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

    List<Monatskosten> monatsKosten(Stelle stelle, LocalDate von, LocalDate bis) {
        if (bis < von)
            throw new IllegalArgumentException("Enddatum ${bis} liegt vor dem Anfang ${von}")
        if (von < stelle.beginn)
            throw new IllegalArgumentException("Argument ${von} liegt vor dem Anfang ${stelle.beginn}")

        def stichtag = von.withDayOfMonth(von.lengthOfMonth())
        def ende = bis.withDayOfMonth(bis.lengthOfMonth())

        List<Monatskosten> kostenListe = new ArrayList<>()
        while (stichtag <= ende) {
            def aktStelle = stelle.am(stichtag)
            def brutto = monatsBrutto(aktStelle.gruppe, aktStelle.stufe, stichtag.year, aktStelle.umfang)
            kostenListe << new Monatskosten(stichtag: stichtag, stelle: stelle, brutto: brutto, sonderzahlung: Constants.euros(0))
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
}
