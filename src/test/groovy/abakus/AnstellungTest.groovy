package abakus

import spock.lang.Specification
import spock.lang.Unroll

import java.time.YearMonth


class AnstellungTest extends Specification {

	def start_2019_12 = YearMonth.of(2019, 12)
	def stelle_e10_1 = Stelle.of(Gruppe.E10, Stufe.eins)

	def "'am' darf nicht vor Anstellungsbeginn liegen"() {

		given:
		def a = Anstellung.of(start_2019_12, stelle_e10_1, start_2019_12.plusYears(10))

		when:
		def vorher = start_2019_12.minusMonths(1)
		a.am(vorher)

		then:
		def ex = thrown IllegalArgumentException
		ex.message == "Keine Stelle zu ${vorher} gefunden (frühest bekannte ist ${start_2019_12})"
	}

	def "einfache Anstellung"() {

		when:
		def a = Anstellung.of(start_2019_12, stelle_e10_1, start_2019_12.plusYears(10))

		then:
		a.am(start_2019_12) == stelle_e10_1
		a.am(start_2019_12.plusMonths(11)) == stelle_e10_1
		a.am(start_2019_12.plusMonths(12)) == Stelle.of(Gruppe.E10, Stufe.zwei)
		a.am(start_2019_12.plusMonths(13)) == Stelle.of(Gruppe.E10, Stufe.zwei)
	}

	def "bei stufe sechs ist schluß"() {

		given:
		def stelle_5 = Stelle.of(Gruppe.E10, Stufe.fünf)

		when:
		def a = Anstellung.of(start_2019_12, stelle_5, start_2019_12.plusYears(30))

		then:
		a.am(start_2019_12.plusYears(20)) == Stelle.of(Gruppe.E10, Stufe.sechs)
	}

	def "calcBaseStellen happy path"() {

		when:
		def a = Anstellung.of(start_2019_12, stelle_e10_1, start_2019_12.plusYears(10))

		then:
		a.calcBaseStellen(2020) == [
			stelle_e10_1,
			stelle_e10_1,
			stelle_e10_1
		]
	}


	@Unroll
	def "months in year von #beginn bis #ende"() {

		expect:
		a.monthsInYear(2019) == resultMonthRange.collect { YearMonth.of(2019, it) }

		where:
		beginn                 | ende                   | resultMonthRange
		YearMonth.of(2018, 12) | YearMonth.of(2019, 7)  | (1..7)
		YearMonth.of(2019, 1)  | YearMonth.of(2019, 7)  | (1..7)
		YearMonth.of(2018, 12) | YearMonth.of(2020, 2)  | (1..12)
		YearMonth.of(2019, 1)  | YearMonth.of(2019, 12) | (1..12)
		YearMonth.of(2019, 3)  | YearMonth.of(2020, 2)  | (3..12)
		YearMonth.of(2019, 3)  | YearMonth.of(2019, 4)  | (3..4)

		a = Anstellung.of(beginn, stelle_e10_1, ende)
	}


	@Unroll
	def "calcBaseStellen Randfall: Einstellung zu Monat #beginnMonth"() {

		expect:
		def start = YearMonth.of(2018, beginnMonth)
		def ans = Anstellung.of(start, stelle_e10_1, start.plusYears(2))

		ans.calcBaseStellen(2018) == Collections.nCopies(noOfStellen, stelle_e10_1)

		where:
		beginnMonth | noOfStellen
		6           | 3
		7           | 3
		8           | 2
		9           | 1
		10          | 1
		11          | 1
	}

	def "calcBaseStellen Randfall mit Aufstieg"() {

		given:
		def start = YearMonth.of(2018, 8)
		Stelle sZwei = Stelle.of(Gruppe.E10, Stufe.zwei)

		when:
		def ans = Anstellung.of(start, stelle_e10_1, start.plusYears(2))

		then:
		ans.calcBaseStellen(2019) == [stelle_e10_1, sZwei, sZwei]
	}
}
