package abakus

import org.javamoney.moneta.Money
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class KostenRechnerTest extends Specification {

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
        Stelle s = new Stelle(gruppe: Gruppe.E13, stufe: Stufe.eins,
                beginn: ld(2021, 1, 1))
        def mk = rechner.monatsKosten(s,
                ld(2021, 1, 1),
                ld(2021, 2, 1))

        then:
        mk.size() == 2
        def fst = mk[0]
        fst == new Monatskosten(stichtag: ld(2021, 1, 31), stelle: s,
                brutto: euros(1.3 * 4074.30), sonderzahlung: euros(0))
        def snd = mk[1]
        snd == new Monatskosten(stichtag: ld(2021, 2, 28), stelle: s,
                brutto: euros(1.3 * 4074.30), sonderzahlung: euros(0))
    }

    def "monatskosten mit aufstieg"() {

        when:
        Stelle s = new Stelle(gruppe: Gruppe.E13, stufe: Stufe.eins,
                beginn: ld(2019, 1, 1))
        def mk = rechner.monatsKosten(s,
                ld(2020, 2, 1),
                ld(2020, 3, 1))

        then:
        mk.size() == 2

        def fst = mk[0]
        Stelle s2 = new Stelle(gruppe: Gruppe.E13, stufe: Stufe.zwei,
                beginn: ld(2020, 1, 1))

        fst == new Monatskosten(stichtag: ld(2020, 2, 29), stelle: s2,
                brutto: euros(1.3 * 4329.43), sonderzahlung: euros(0))
    }

    def "sonderzuschlag zero für Nicht-November"() {

        when:
        def stichtag = ld(2021, 10, 1)
        def bis = ld(2021, 12, 30)

        then:
        rechner.sonderzahlung(stichtag, bis, null) == euros(0)
    }

    def "sonderzuschlag zero falls Ende vor Dezember"() {

        when:
        def stichtag = ld(2021, 11, 1)
        def bis = ld(2021, 11, 30)

        then:
        rechner.sonderzahlung(stichtag, bis, null) == euros(0)
    }

    def "sonderzuschlag einfach"() {

        when:
        def stichtag = ld(2020, 11, 1)
        def bis = ld(2021, 1, 30)

        Stelle s = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.eins,
                beginn: ld(2018, 4, 1))

        then:
        rechner.sonderzahlung(stichtag, bis, s) == euros(1)
    }

    def "calcBaseStellen happy path"() {

        when:
        Stelle s = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.drei,
                beginn: ld(2018, 4, 1))

        then:
        rechner.calcBaseStellen(2018, s) == [s, s, s]
    }

    @Unroll
    def "calcBaseStellen Randfall ab Monat #beginnMonth"() {

        expect:
        Stelle s = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.drei,
                beginn: ld(2018, beginnMonth, 1))
        rechner.calcBaseStellen(2018, s) == Collections.nCopies(noOfStellen, s)

        where:
        beginnMonth | noOfStellen
        6           | 3
        7           | 3
        8           | 2
        9           | 1
        10          | 1
        11          | 1
    }

    def "calcBaseStellen Randfall mit Aufstieg"() {

        when:
        Stelle s = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.eins,
                beginn: ld(2018, 8, 1))

        then:
        Stelle sZwei = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.zwei,
                beginn: ld(2019, 8, 1))

        rechner.calcBaseStellen(2019, s) == [s, sZwei, sZwei]
    }

    def ld(int year, int month, int dayOfMonth) {
        LocalDate.of(year, month, dayOfMonth)
    }

    def euros(Number amount) {
        Constants.euros(amount)
    }
}
