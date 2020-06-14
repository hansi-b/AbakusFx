package abakus

import groovy.transform.Immutable
import org.javamoney.moneta.Money

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
}

@Immutable
class GruppeUndJahr {
    Gruppe gruppe
    int jahr
}

@Immutable
class Gehälter {
    BigDecimal sonderzahlung
    Map<Stufe, Money> bruttos
}

@Immutable
class Tarif {
    Map<GruppeUndJahr, Gehälter> gehälter
}
