package abakus

import groovy.transform.Immutable
import org.javamoney.moneta.Money

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
