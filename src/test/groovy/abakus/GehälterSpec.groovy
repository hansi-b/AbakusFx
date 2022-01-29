package abakus

import static abakus.Gruppe.*

import java.time.YearMonth

import static abakus.Constants.euros

import org.javamoney.moneta.Money
import spock.lang.Specification

class GehälterSpec extends Specification {

	def oneJsz = new BigDecimal(1);
	def oneBruttos = [ (Stufe.drei) : euros(3) ] as EnumMap

	def otherBruttos = [ (Stufe.zwei) : euros(2) ] as EnumMap
	def otherJsz = new BigDecimal(9);

	def cmp = Gehälter.gültigkeitUndGruppeComparator

	def ym2018 = YearMonth.of(2018, 1)
	def ym2019 = YearMonth.of(2019, 1)

	def "gruppe und jahr reichen für gleichheit"() {

		expect:
		cmp.compare(
				new Gehälter(E10, ym2019, oneJsz, oneBruttos),
				new Gehälter(E10, ym2019, otherJsz, otherBruttos)) == 0
	}

	def "gruppe trumpft jahr"() {

		expect:
		cmp.compare(
				new Gehälter(E10, ym2018, oneJsz, oneBruttos),
				new Gehälter(E13, ym2019, oneJsz, oneBruttos)) == -1
	}

	def "jahr wird bei gruppengleichheit benutzt"() {

		expect:
		cmp.compare(
				new Gehälter(E10, ym2019, oneJsz, oneBruttos),
				new Gehälter(E10, ym2018, oneJsz, oneBruttos)) == 1
	}
}
