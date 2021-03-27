package abakus

import static abakus.Gruppe.*
import static abakus.Stufe.*

import org.javamoney.moneta.Money
import spock.lang.Specification

class TarifSpec extends Specification {

	def tarif = new ÖtvCsvParser().parseTarif()

	def "Stichproben aus CSV werden richtig gelesen"() {
		expect:
		tarif.brutto(E8, eins, 2022).money == Money.of(2866.21, Constants.eur)
		tarif.brutto(E10, zwei, 2021).money == Money.of(3662.23, Constants.eur)
		tarif.brutto(E13, sechs, 2020).money == Money.of(5798.14, Constants.eur)
	}

	def "letztes Brutto im Tarif wird auch für später genommen"() {
		expect:
		// if this fails, the ötv.csv probably got updated for later years
		tarif.brutto(E13, vier, 2023) == tarif.brutto(E13, vier, 2024)
	}

	def "Stichprobe für Sonderzahlung"() {
		expect:
		tarif.sonderzahlung(E13, eins, 2021).money ==  Money.of(4074.3, Constants.eur).multiply(46.47/100)
	}
}
