package abakus

import groovy.transform.Immutable

import java.time.Month
import java.time.YearMonth

enum Gruppe {
    E10, E13
}

enum Stufe {
    eins, zwei, drei, vier, fünf, sechs

    String asString() {
        "${ordinal() + 1}"
    }

    static Stufe fromString(String ordStr) {
        values()[Integer.valueOf(ordStr) - 1]
    }

    Stufe nächste() {
        ordinal() < sechs.ordinal() ? values()[ordinal() + 1] : sechs
    }

    YearMonth nächsterAufstieg(YearMonth seit) {
        return seit.plusYears(ordinal() + 1)
    }

    Stufe stufeAm(YearMonth seit, YearMonth am) {

        def n = nächste()
        if (n == this) return this

        def aufstieg = nächsterAufstieg(seit)
        return aufstieg > am ? this : n.stufeAm(aufstieg, am)
    }
}

@Immutable
class Stelle {
    Gruppe gruppe
    Stufe stufe
    BigDecimal umfang = BigDecimal.valueOf(100)

    static Stelle of(Gruppe g, Stufe s, int umfang = 100) {
        new Stelle(gruppe: g, stufe: s, umfang: BigDecimal.valueOf(umfang))
    }
}

/**
 * Represents a series and projection of Stellen
 */
class Anstellung {

    private final SortedMap<YearMonth, Stelle> stelleByBeginn = new TreeMap<>()
    private YearMonth ende

    static Anstellung of(YearMonth beginn, Stelle antrittsStelle, YearMonth ende) {
        def a = new Anstellung(ende: ende)
        a.add(beginn, antrittsStelle)
        return a
    }

    YearMonth getBeginn() {
        stelleByBeginn.firstKey()
    }

    private void add(YearMonth beginn, Stelle antrittsStelle) {
        if (beginn > ende)
            throw new IllegalArgumentException("Stellenbeginn ${beginn} liegt nach dem Anstellungsende ${ende}")

        assert !stelleByBeginn.containsKey(beginn)
        stelleByBeginn[beginn] = antrittsStelle
    }

    Stelle am(YearMonth ym) {

        if (ym > ende)
            throw new IllegalArgumentException("Argument ${ym} liegt nach dem Anstellungsende ${ende}")

        def entry = stelleByBeginn.find { it.key <= ym }
        if (!entry)
            throw new IllegalArgumentException("Argument ${ym} liegt vor dem Anstellungsbeginn ${beginn}")
        def (beginn, stelle) = [entry.key, entry.value]

        def neueStufe = stelle.stufe.stufeAm(beginn, ym)
        neueStufe == stelle.stufe ?
                stelle : new Stelle(stelle.gruppe, neueStufe, stelle.umfang)
    }

    SortedMap<YearMonth, Stelle> monatsStellen(YearMonth von, YearMonth bis) {

        if (bis < von)
            throw new IllegalArgumentException("Enddatum ${bis} liegt vor dem Anfang ${von}")

        SortedMap<YearMonth, Stelle> stellenByYm = new TreeMap<>()
        def current = von
        while (current <= bis) {
            stellenByYm[current] = am(current)
            current = current.plusMonths(1)
        }

        return stellenByYm
    }

    def monthsInYear(int year) {
        (1..12).collect { YearMonth.of(year, it) }.findAll {
            beginn <= it && it <= ende
        }
    }

    /**
     * @return the Stellen of the argument year for the Sonderzahlung
     */
    def calcBaseStellen(int year) {

        assert beginn <= YearMonth.of(year, Month.NOVEMBER)

        def months = [Month.JULY, Month.AUGUST, Month.SEPTEMBER]
                .collect { YearMonth.of(year, it.value) }
                .findAll { it >= beginn }
                ?: [YearMonth.of(year, beginn.month.value)]
        return months.collect { am(it) }
    }

    /**
     * @return die Stellen im gegebenen Jahr, die laut Verordnung die Grundlage
     * für die Berechnung der Sonderzahlung bilden
     */
    def sonderzahlungBaseStellen(int jahr) {
        null
    }

    def anzahlMonate(int jahr) {
        12
    }
}
