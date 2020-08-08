package abakus

import groovy.transform.Immutable

import java.time.LocalDate

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

    LocalDate nächsterAufstieg(LocalDate seit) {
        return seit.plusYears(ordinal() + 1)
    }

    Stufe stufeAm(LocalDate seit, LocalDate am) {

        def n = nächste()
        if (n == this) return this

        def aufstieg = nächsterAufstieg(seit)
        return aufstieg.isAfter(am) ? this : n.stufeAm(aufstieg, am)
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

    private final SortedMap<LocalDate, Stelle> stelleByBeginn = new TreeMap<>()

    static Anstellung of(LocalDate beginn, Stelle antrittsStelle) {
        def a = new Anstellung()
        a.add(beginn, antrittsStelle)
        return a
    }

    LocalDate getBeginn() {
        stelleByBeginn.firstKey()
    }

    private void add(LocalDate beginn, Stelle antrittsStelle) {
        assert !stelleByBeginn.containsKey(beginn)
        stelleByBeginn[beginn] = antrittsStelle
    }

    Stelle am(LocalDate datum) {
        def entry = stelleByBeginn.find { it.key.isBefore(datum) }
        if (!entry)
            throw new IllegalArgumentException("Argument ${datum} liegt vor dem ersten Anfang ${beginn}")
        def (beginn, stelle) = [entry.key, entry.value]

        def neueStufe = stelle.stufe.stufeAm(beginn, datum)
        neueStufe == stelle.stufe ?
                stelle : new Stelle(stelle.gruppe, neueStufe, stelle.umfang)
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
