package abakus

import org.javamoney.moneta.Money
import spock.lang.Specification

import java.time.LocalDate

class KostenRechnerTest extends Specification {

    def "kosten beinhalten arbeitgeberzuschlag"() {

        given:
        Tarif t = new ÖtvCsvParser().parseTarif()

        when:
        Money m = new KostenRechner(t).monatsBrutto(Gruppe.E10, Stufe.drei, 2020, BigDecimal.valueOf(80))

        then:
        m.getNumber() == 3880.76 * 1.3 * 0.8
    }

    def "monatskosten"() {

        given:
        KostenRechner r = new KostenRechner(new ÖtvCsvParser().parseTarif())

        when:
        Stelle s = new Stelle(gruppe: Gruppe.E13, stufe: Stufe.eins,
                beginn: LocalDate.of(2021, 1, 1))
        def mk = r.monatsKosten(s,
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 2, 1))

        then:
        mk.size() == 2
        mk[0] == new Monatskosten(stichtag: LocalDate.of(2021, 1, 31), stelle: s,
                brutto: Constants.euros(1.3 * 4074.30), sonderzahlung: Constants.euros(0))
        mk[1] == new Monatskosten(stichtag: LocalDate.of(2021, 2, 28), stelle: s,
                brutto: Constants.euros(1.3 * 4074.30), sonderzahlung: Constants.euros(0))
    }
}
