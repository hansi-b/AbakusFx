package abakus

import spock.lang.Specification

import java.time.LocalDate

class StelleTest extends Specification {

    def "können eine Stufe aufsteigen"() {

        given:
        def start = LocalDate.of(1959, 12, 1)

        when:
        def stelle = new Stelle(Gruppe.E10, Stufe.eins, start, 100)

        then:
        stelle.am(start.plusMonths(11)) == stelle
        stelle.am(start.plusMonths(12)) == new Stelle(Gruppe.E10, Stufe.zwei, start.plusMonths(12), 100)
        stelle.am(start.plusMonths(13)) == new Stelle(Gruppe.E10, Stufe.zwei, start.plusMonths(12), 100)
    }

    def "können stufen überspringen"() {

        given:
        def start = LocalDate.of(1959, 12, 1)

        when:
        def stelle = new Stelle(Gruppe.E10, Stufe.eins, start, 100)

        then:
        stelle.am(start.plusYears(4)) == new Stelle(Gruppe.E10, Stufe.drei, start.plusYears(1 + 2), 100)
    }

    def "bei stufe sechs ist schluß"() {

        given:
        def start = LocalDate.of(1959, 12, 1)

        when:
        def stelle = new Stelle(Gruppe.E10, Stufe.fünf, start, 100)

        then:
        stelle.am(start.plusYears(20)) == new Stelle(Gruppe.E10, Stufe.sechs, start.plusYears(5), 100)
    }
}
