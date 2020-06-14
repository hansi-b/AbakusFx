package abakus

import groovy.transform.Immutable
import org.javamoney.moneta.Money

import javax.money.CurrencyUnit
import javax.money.Monetary
import java.text.DecimalFormat


enum Gruppe {
    E10, E13
}

enum Stufe {
    eins, zwei, drei, vier, fünf, sechs
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
