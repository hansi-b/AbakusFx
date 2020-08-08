package abakus

import spock.lang.Specification

import static abakus.Constants.startOfMonth

class AnstellungTest extends Specification {

    def start = startOfMonth(2019, 12)
    def stelle = Stelle.of(Gruppe.E10, Stufe.eins)

    def "'am' darf nicht vor Anstellungsbeginn liegen"() {

        given:
        def a = Anstellung.of(start, stelle)

        when:
        def vorher = start.minusMonths(1)
        a.am(vorher)

        then:
        def ex = thrown IllegalArgumentException
        ex.message == "Argument ${vorher} liegt vor dem ersten Anfang ${start}"
    }

    def "einfache Anstellung"() {

        when:
        def a = Anstellung.of(start, stelle)

        then:
        a.am(start.plusMonths(11)) == stelle
        a.am(start.plusMonths(12)) == Stelle.of(Gruppe.E10, Stufe.zwei)
        a.am(start.plusMonths(13)) == Stelle.of(Gruppe.E10, Stufe.zwei)
    }

    def "bei stufe sechs ist schluß"() {

        given:
        def stelle_5 = Stelle.of(Gruppe.E10, Stufe.fünf)

        when:
        def a = Anstellung.of(start, stelle_5)

        then:
        a.am(start.plusYears(20)) == Stelle.of(Gruppe.E10, Stufe.sechs)
    }
}
