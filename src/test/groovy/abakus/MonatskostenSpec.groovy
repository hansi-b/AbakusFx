package abakus

import java.time.YearMonth

import spock.lang.Specification

public class MonatskostenSpec extends Specification {

	YearMonth ym1 = YearMonth.of(2021, 10)
	YearMonth ym2 = YearMonth.of(2022, 2)

	Stelle s1 = Mock(Stelle)
	Stelle s2 = Mock(Stelle)

	ExplainedMoney ex1 = Mock(ExplainedMoney)
	ExplainedMoney ex2 = Mock(ExplainedMoney)

	def "equality der Monatskosten"() {

		expect:
		def mk = new Monatskosten(ym1, s1, ex1)

		mk.equals(mk)
		!mk.equals(null)

		!mk.equals(new Monatskosten(ym2, s1, ex1))
		!mk.equals(new Monatskosten(ym1, s2, ex1))
		!mk.equals(new Monatskosten(ym1, s1, ex2))
		mk.equals(new Monatskosten(ym1, s1, ex1))
	}
}
