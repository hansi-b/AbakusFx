package abakus;

import spock.lang.Shared
import spock.lang.Specification

class ExplainedMoneySpec extends Specification {

	def "einfache Zahl"() {

		when:
		def em = exM(10.6, 'wert')

		then:
		em.money == Constants.euros(10.6)
		em.explained == '10,60 € wert'
	}

	def "simple addition"() {

		expect:
		def em = fst.add(snd)
		em.explained == resEx
		em.money == Constants.euros(resSum)

		where:
		fst | snd | resSum | resEx
		exM(2.6, '1st') | exM(1.3, '2nd') |  2.6+1.3 | '2,60 € 1st + 1,30 € 2nd'
		exM(1.3, '2nd') | exM(2.6, '1st') |  2.6+1.3 | '1,30 € 2nd + 2,60 € 1st'
	}

	def "multiply percent"() {

		expect:
		def em = fst.multiplyPercent(perc, ex)
		em.explained == resEx
		em.money == Constants.euros(res)

		where:
		fst | perc | ex | res | resEx
		exM(2.6, '1st') | 50 | "more" | 1.3 | '2,60 € 1st × 50% more'
		exM(6, '2nd') | 20 | "Aufschlag" | 1.2 | '6,00 € 2nd × 20% Aufschlag'
	}

	def "add percent"() {

		expect:
		def em = fst.addPercent(snd, ex)
		em.explained == resEx
		em.money == Constants.euros(res)

		where:
		fst | snd | ex | res | resEx
		exM(2.6, '1st') | 50 | "added" | 3.9 | '2,60 € 1st + 50% added'
		exM(6, '2nd') | 20 | "Aufschlag" | 7.2 | '6,00 € 2nd + 20% Aufschlag'
	}

	def "equality"() {

		expect:
		def em = exM(5.6, 'simple')
		em.equals(em)
		!em.equals(null)
		em.equals(exM(5.6, 'simple'))
	}

	def exM(n, s) {
		ExplainedMoney.of(Constants.euros(n), s)
	}
}
