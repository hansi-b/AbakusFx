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
        Stufe.values()[Integer.valueOf(ordStr) - 1]
    }

    Stufe nächste() {
        ordinal() < sechs.ordinal() ? Stufe.values()[ordinal() + 1] : sechs
    }

    LocalDate nächsterAufstieg(LocalDate seit) {
        return seit.plusYears(ordinal() + 1)
    }

    def stufeAm(LocalDate seit, LocalDate am) {
        def aufstieg = nächsterAufstieg(seit)
        if (nächste() == this || aufstieg.isAfter(am))
            return [this, seit]
        return nächste().stufeAm(aufstieg, am)
    }
}

@Immutable
class Stelle {
    Gruppe gruppe
    Stufe stufe
    LocalDate beginn
    BigDecimal umfang = 100

    Stelle am(LocalDate datum) {
        def (Stufe nächsteStufe, LocalDate aufstiegsDatum) = stufe.stufeAm(beginn, datum)
        nächsteStufe == stufe ?
                this : new Stelle(gruppe, nächsteStufe, aufstiegsDatum, umfang)
    }
}
