package abakus

import static abakus.Constants.*

import java.time.YearMonth

import javax.money.Monetary

import org.javamoney.moneta.Money

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class KostenRechnerSpec extends Specification {

	@Shared BigDecimal agz = BigDecimal.valueOf(30.0)
	@Shared Anstellung ans = Anstellung.of( YearMonth.of(2021, 1), Stelle.of(Gruppe.E13, Stufe.eins), YearMonth.of(2023, 1), agz)

	KostenRechner rechner = new KostenRechner(new ÖtvCsvParser().parseTarif())

	def "monatsBrutto beinhaltet nicht arbeitgeberzuschlag"() {

		when:
		ExplainedMoney m = rechner.monatsBrutto(Stelle.of(Gruppe.E10, Stufe.drei, 80), YearMonth.of(2020, 2))

		then:
		m.money.getNumber() == 3880.76 * 0.8
	}

	def "einfache monatskosten"() {

		when:
		def start = YearMonth.of(2021, 1)
		def end = YearMonth.of(2021, 2)

		def mk = rechner.monatsKosten(ans, start, end)

		then:
		mk.size() == 2

		mk[0].stichtag == start
		mk[0].stelle == Stelle.of(Gruppe.E13, Stufe.eins)
		mk[0].kosten.money() == euros(1.3 * 4074.30)

		mk[1].stichtag == end
		mk[1].stelle == Stelle.of(Gruppe.E13, Stufe.eins)
		mk[1].kosten.money() == euros(1.3 * 4074.30)
	}

	def "einfache monatskosten mit anderem agz"() {

		given:
		def ans = Anstellung.of( YearMonth.of(2021, 1), Stelle.of(Gruppe.E13, Stufe.eins),
				YearMonth.of(2023, 1), BigDecimal.valueOf(10.0))

		when:
		def start = YearMonth.of(2021, 1)
		def end = YearMonth.of(2021, 1)

		def mk = rechner.monatsKosten(ans, start, end)

		then:
		mk.size() == 1

		mk[0].stichtag == start
		mk[0].stelle == Stelle.of(Gruppe.E13, Stufe.eins)
		mk[0].kosten.money() == euros(1.1 * 4074.30)
	}

	def "monatskosten mit aufstieg"() {

		when:
		def start = YearMonth.of(2021, 12)
		def end = YearMonth.of(2022, 1)
		def mk = rechner.monatsKosten(ans, start, end)

		then:
		mk.size() == 2

		mk[0].stichtag == start
		mk[0].stelle == Stelle.of(Gruppe.E13, Stufe.eins)
		mk[0].kosten.money() == euros(1.3 * 4074.30)

		mk[1].stichtag == end
		mk[1].stelle == Stelle.of(Gruppe.E13, Stufe.zwei)
		mk[1].kosten.money() == euros(1.3 *  4385.28)
	}

	def "monatskosten über Weiterbeschäftigung"() {

		when:
		Anstellung weiter = Anstellung.weiter( YearMonth.of(2021, 1),
				Stelle.of(Gruppe.E13, Stufe.eins),
				YearMonth.of(2022, 1), 70, YearMonth.of(2023, 1), agz)

		def start = YearMonth.of(2021, 12)
		def end = YearMonth.of(2022, 1)
		def mk = rechner.monatsKosten(weiter, start, end)

		then:
		mk.size() == 2
		mk[0].stichtag == start
		mk[0].stelle == Stelle.of(Gruppe.E13, Stufe.eins)
		mk[0].kosten.money() == euros(1.3 * 4074.30)

		mk[1].stichtag == end
		mk[1].stelle == Stelle.of(Gruppe.E13, Stufe.zwei, 70)
		mk[1].kosten.money() == euros(1.3 * 4385.28 * 0.7)
	}

	def "sonderzuschlag zero für Nicht-November"() {

		when:
		def stichtag = YearMonth.of(2021, 10)

		then:
		rechner.sonderzahlung(stichtag, ans) == null
	}

	def "sonderzuschlag zero falls Ende vor Dezember"() {

		when:
		def stichtag = YearMonth.of(2021, 11)
		Anstellung ans = Anstellung.of(YearMonth.of(2021, 1),
				Stelle.of( Gruppe.E13,  Stufe.eins),
				YearMonth.of(2021, 11), agz)

		then:
		rechner.sonderzahlung(stichtag, ans).money == euros(0)
	}

	def "sonderzuschlag einfach - kein agz"() {

		when:
		def start = YearMonth.of(2020, 1)
		def stichtag = YearMonth.of(2020, 11)
		Anstellung ans = Anstellung.of(start,
				Stelle.of( Gruppe.E10,  Stufe.eins),
				start.plusYears(2), agz)
		then:
		// E10, Stufe 1, 2020: 3.367,04, Faktor 75,31
		rechner.sonderzahlung(stichtag, ans).money == eurosRounded(0.7531 * 3367.04)
	}

	@Unroll
	def "sonderzuschlag anteilig ab #start"() {

		given:
		def stichtag = YearMonth.of(2020, 11)
		def fullSalary = euros(0.7531 * 3367.04)

		expect:
		// E10, Stufe 1, 2020: 3.367,04, Faktor 75,31
		def expected = eurosRounded(fullSalary * anteil)
		def actual = rechner.sonderzahlung(stichtag, ans).money
		println "Expected ${expected}"
		println "Actual ${actual}"
		actual == expected

		where:
		start << (1..11).collect { YearMonth.of(2020, it) }
		anteil = (1 + 12 - start.month.value) / 12
		ans = Anstellung.of(start,
				Stelle.of(Gruppe.E10, Stufe.eins),
				start.plusYears(2), agz)
	}
}
