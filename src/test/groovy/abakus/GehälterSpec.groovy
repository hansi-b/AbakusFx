package abakus

import static abakus.Gruppe.*
import static abakus.Constants.euros

import org.javamoney.moneta.Money
import spock.lang.Specification

class GehälterSpec extends Specification {

	def oneJsz = new BigDecimal(1);
	def oneBruttos = [ drei : euros(3) ]

	def otherBruttos = [ zwei : euros(2) ]
	def otherJsz = new BigDecimal(9);

	def cmp = Gehälter.jahrUndGruppeComparator

	def "gruppe und jahr reichen für gleichheit"() {

		expect:
		cmp.compare(
				new Gehälter(E10, 2019, oneJsz, oneBruttos),
				new Gehälter(E10, 2019, otherJsz, otherBruttos)) == 0
	}

	def "gruppe trumpft jahr"() {

		expect:
		cmp.compare(
				new Gehälter(E10, 2018, oneJsz, oneBruttos),
				new Gehälter(E13, 2019, oneJsz, oneBruttos)) == -1
	}

	def "jahr wird bei gruppengleichheit benutzt"() {

		expect:
		cmp.compare(
				new Gehälter(E10, 2019, oneJsz, oneBruttos),
				new Gehälter(E10, 2018, oneJsz, oneBruttos)) == 1
	}
}
