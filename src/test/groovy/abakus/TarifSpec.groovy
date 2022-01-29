package abakus

import static abakus.Gruppe.*
import static abakus.Stufe.*

import java.time.YearMonth

import org.javamoney.moneta.Money

import spock.lang.Specification

class TarifSpec extends Specification {

	def tarif = new ÖtvCsvParser().parseTarif()

	def ym2020 = YearMonth.of(2020, 1)
	def ym2021 = YearMonth.of(2021, 1)
	def ym2022 = YearMonth.of(2022, 1)
	def ym2023 = YearMonth.of(2023, 1)
	def ym2024 = YearMonth.of(2024, 1)

	def "Stichproben aus CSV werden richtig gelesen"() {
		expect:
		tarif.brutto(E8, eins, ym2022).money == Money.of(2866.21, Constants.eur)
		tarif.brutto(E10, zwei, ym2021).money == Money.of(3662.23, Constants.eur)
		tarif.brutto(E13, sechs, ym2020).money == Money.of(5798.14, Constants.eur)
	}

	def "letztes Brutto im Tarif wird auch für später genommen"() {
		expect:
		// if this fails, the ötv.csv probably got updated for later years
		tarif.brutto(E13, vier, ym2023) == tarif.brutto(E13, vier, ym2024)
	}

	def "Stichprobe für Sonderzahlung"() {
		expect:
		tarif.sonderzahlung(E13, eins, 2021).money ==  Money.of(4074.3, Constants.eur).multiply(46.47/100)
	}
}
