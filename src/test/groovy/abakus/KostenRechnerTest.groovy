package abakus

import org.javamoney.moneta.Money
import spock.lang.Specification
import spock.lang.Unroll

import static abakus.Constants.*

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
        Stelle s = new Stelle(gruppe: Gruppe.E13, stufe: Stufe.eins, beginn: startOfMonth(2021, 1))
        def mk = rechner.monatsKosten(s, startOfMonth(2021, 1), endOfMonth(2021, 2))

        then:
        mk.size() == 2
        def fst = mk[0]
        fst == new Monatskosten(stichtag: endOfMonth(2021, 1), stelle: s,
                brutto: euros(1.3 * 4074.30), sonderzahlung: euros(0))
        def snd = mk[1]
        snd == new Monatskosten(stichtag: endOfMonth(2021, 2), stelle: s,
                brutto: euros(1.3 * 4074.30), sonderzahlung: euros(0))
    }

    def "monatskosten mit aufstieg"() {

        when:
        Stelle s = new Stelle(gruppe: Gruppe.E13, stufe: Stufe.eins, beginn: startOfMonth(2019, 1))
        def mk = rechner.monatsKosten(s, startOfMonth(2020, 2), endOfMonth(2020, 3))

        then:
        mk.size() == 2

        def fst = mk[0]
        Stelle s2 = new Stelle(gruppe: Gruppe.E13, stufe: Stufe.zwei, beginn: startOfMonth(2020, 1))

        fst == new Monatskosten(stichtag: endOfMonth(2020, 2), stelle: s2,
                brutto: euros(1.3 * 4329.43), sonderzahlung: euros(0))
    }

    def "sonderzuschlag zero für Nicht-November"() {

        when:
        def stichtag = startOfMonth(2021, 10)
        def bis = endOfMonth(2021, 12)

        then:
        rechner.sonderzahlung(stichtag, bis, null) == euros(0)
    }

    def "sonderzuschlag zero falls Ende vor Dezember"() {

        when:
        def stichtag = startOfMonth(2021, 11)
        def bis = endOfMonth(2021, 11)

        then:
        rechner.sonderzahlung(stichtag, bis, null) == euros(0)
    }

    def "sonderzuschlag einfach"() {

        when:
        Stelle s = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.eins, beginn: startOfMonth(2020, 1))

        def stichtag = startOfMonth(2020, 11)
        def bis = endOfMonth(2021, 1)

        then:
        // E10, Stufe 1, 2020: 3.367,04, Faktor 75,31
        rechner.sonderzahlung(stichtag, bis, s) == euros(1.3 * 0.7531 * 3367.04)
    }

    def "calcBaseStellen happy path"() {

        when:
        Stelle s = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.drei, beginn: startOfMonth(2018, 4))

        then:
        rechner.calcBaseStellen(2018, s) == [s, s, s]
    }

    @Unroll
    def "calcBaseStellen Randfall ab Monat #beginnMonth"() {

        expect:
        Stelle s = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.drei, beginn: startOfMonth(2018, beginnMonth))
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
        Stelle s = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.eins, beginn: startOfMonth(2018, 8))

        then:
        Stelle sZwei = new Stelle(gruppe: Gruppe.E10, stufe: Stufe.zwei, beginn: startOfMonth(2019, 8))

        rechner.calcBaseStellen(2019, s) == [s, sZwei, sZwei]
    }
}
