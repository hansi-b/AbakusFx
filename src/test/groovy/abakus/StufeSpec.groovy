package abakus

import spock.lang.Specification
import spock.lang.Unroll

import java.time.YearMonth


class StufeSpec extends Specification {

    @Unroll
    def "Stufe 1 nach #plusMonths Monaten ist #neueStufe"() {

        expect:
        def start = YearMonth.of(2019, 1)
        Stufe.eins.stufeAm(start, start.plusMonths(plusMonths)) == neueStufe

        where:
        plusMonths | neueStufe
        10         | Stufe.eins
        11         | Stufe.eins
        12         | Stufe.zwei
        13         | Stufe.zwei
    }

    @Unroll
    def "Stufe 1 nach #plusYears Jahren spring zu #neueStufe"() {

        expect:
        def start = YearMonth.of(2019, 1)
        Stufe.eins.stufeAm(start, start.plusYears(plusYears)) == neueStufe

        where:
        plusYears     | neueStufe
        1 + 2         | Stufe.drei
        1 + 2 + 3     | Stufe.vier
        1 + 2 + 3 + 4 | Stufe.fünf
    }

}
