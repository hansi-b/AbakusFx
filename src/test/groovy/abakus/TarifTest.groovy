package abakus

import org.javamoney.moneta.Money
import spock.lang.Specification

class TarifTest extends Specification {

	def "bruttos werden richtig gelesen"() {
		when:
		def tarif = new ÖtvCsvParser().parseTarif()

		then:
		tarif.brutto(Gruppe.E8, Stufe.eins, 2022) == Money.of(2866.21, Constants.eur)
		tarif.brutto(Gruppe.E10, Stufe.zwei, 2021) == Money.of(3662.23, Constants.eur)
		tarif.brutto(Gruppe.E13, Stufe.sechs, 2020) == Money.of(5798.14, Constants.eur)
	}

	def "bruttos für 2022 wird auch für später genommen"() {
		when:
		def tarif = new ÖtvCsvParser().parseTarif()

		then:
		tarif.brutto(Gruppe.E13, Stufe.vier, 2023) == tarif.brutto(Gruppe.E13, Stufe.vier, 2022)
	}
}
