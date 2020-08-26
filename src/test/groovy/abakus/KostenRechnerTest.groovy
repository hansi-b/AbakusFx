package abakus

import org.javamoney.moneta.Money
import spock.lang.Specification
import spock.lang.Unroll

import java.time.YearMonth

import static abakus.Constants.euros

class KostenRechnerTest extends Specification {

    Anstellung ans = Anstellung.of(YearMonth.of(2021, 1),
            new Stelle(gruppe: Gruppe.E13, stufe: Stufe.eins),
            YearMonth.of(2023, 1))
    KostenRechner rechner = new KostenRechner(new ÖtvCsvParser().parseTarif())

    def "kosten beinhalten arbeitgeberzuschlag"() {

        given:
        Tarif t = new ÖtvCsvParser().parseTarif()

        when:
        Money m = new KostenRechner(t).monatsBrutto(Gruppe.E10, Stufe.drei, 2020, BigDecimal.valueOf(80))

        then:
        m.getNumber() == 3880.76 * 1.3 * 0.8
    }

    def "einfache monatskosten"() {

        given:
        KostenRechner rechner = new KostenRechner(new ÖtvCsvParser().parseTarif())

        when:
        def start = YearMonth.of(2021, 1)
        def end = YearMonth.of(2021, 2)

        def mk = rechner.monatsKosten(ans, start, end)

        then:
        mk.size() == 2

        mk[0] == new Monatskosten(stichtag: start,
                stelle: Stelle.of(Gruppe.E13, Stufe.eins),
                brutto: euros(1.3 * 4074.30), sonderzahlung: euros(0))

        mk[1] == new Monatskosten(stichtag: end,
                stelle: Stelle.of(Gruppe.E13, Stufe.eins),
                brutto: euros(1.3 * 4074.30), sonderzahlung: euros(0))
    }

    def "monatskosten mit aufstieg"() {

        when:
        def start = YearMonth.of(2021, 12)
        def end = YearMonth.of(2022, 1)
        def mk = rechner.monatsKosten(ans, start, end)

        then:
        mk.size() == 2

        mk[0] == new Monatskosten(stichtag: start,
                stelle: Stelle.of(Gruppe.E13, Stufe.eins),
                brutto: euros(1.3 * 4074.30), sonderzahlung: euros(0))

        mk[1] == new Monatskosten(stichtag: end,
                stelle: Stelle.of(Gruppe.E13, Stufe.zwei),
                brutto: euros(1.3 * 4385.28), sonderzahlung: euros(0))
    }

    def "sonderzuschlag zero für Nicht-November"() {

        when:
        def stichtag = YearMonth.of(2021, 10)

        then:
        rechner.sonderzahlung(stichtag, ans) == euros(0)
    }

    def "sonderzuschlag zero falls Ende vor Dezember"() {

        when:
        def stichtag = YearMonth.of(2021, 11)
        Anstellung ans = Anstellung.of(YearMonth.of(2021, 1),
                new Stelle(gruppe: Gruppe.E13, stufe: Stufe.eins),
                YearMonth.of(2021, 11))

        then:
        rechner.sonderzahlung(stichtag, ans) == euros(0)
    }

    def "sonderzuschlag einfach"() {

        when:
        def start = YearMonth.of(2020, 1)
        def stichtag = YearMonth.of(2020, 11)
        Anstellung ans = Anstellung.of(start,
                new Stelle(gruppe: Gruppe.E10, stufe: Stufe.eins),
                start.plusYears(2))
        then:
        // E10, Stufe 1, 2020: 3.367,04, Faktor 75,31
        rechner.sonderzahlung(stichtag, ans) == euros(1.3 * 0.7531 * 3367.04)
    }

    @Unroll
    def "sonderzuschlag anteilig ab #start"() {

        given:
        def stichtag = YearMonth.of(2020, 11)
        def fullSalary = euros(1.3 * 0.7531 * 3367.04)

        expect:
        // E10, Stufe 1, 2020: 3.367,04, Faktor 75,31
        rechner.sonderzahlung(stichtag, ans) == fullSalary * anteil

        where:
        start << (1..11).collect { YearMonth.of(2020, it) }
        anteil = (1 + 12 - start.month.value) / 12
        ans = Anstellung.of(start,
                new Stelle(gruppe: Gruppe.E10, stufe: Stufe.eins),
                start.plusYears(2))
    }
}
