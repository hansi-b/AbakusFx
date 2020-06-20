package abakus

import groovy.transform.Immutable
import org.javamoney.moneta.Money

@Immutable
class Gehälter {
    BigDecimal sonderzahlungProzent
    Map<Stufe, Money> bruttos
}

@Immutable
class Tarif {

    private Map<Gruppe, Map<Integer, Gehälter>> gehälter

    Money brutto(Gruppe gruppe, Stufe stufe, int jahr) {
        lookupYearTolerant(gruppe, jahr).bruttos.get(stufe)
    }

    Money sonderzahlung(Gruppe gruppe, Stufe stufe, int jahr) {
        def geh = lookupYearTolerant(gruppe, jahr)
        geh.bruttos.get(stufe) * geh.sonderzahlungProzent
    }

    private Gehälter lookupYearTolerant(Gruppe gruppe, int jahr) {
        def gruppenGehälter = gehälter.get(gruppe)
        def jahre = gruppenGehälter.keySet()

        if (jahre.contains(jahr))
            return gruppenGehälter.get(jahr)
        if (jahr > jahre.max())
            return gruppenGehälter.get(jahre.max())

        throw new IllegalArgumentException("Keine Gehälter für das Jahr ${jahr} (frühestes ist ${jahre.min()})")
    }
}
