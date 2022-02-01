package abakus

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static abakus.Gruppe.*
import static abakus.Stufe.*

import java.time.YearMonth


class AnstellungSpec extends Specification {

	@Shared
	def agz = BigDecimal.valueOf(30)

	def start_2019_12 = YearMonth.of(2019, 12)
	def stelle_e10_1 = Stelle.of(E10, eins)
	def stelle_e10_3 = Stelle.of(E10, drei)

	def "'am' darf nicht vor Anstellungsbeginn liegen"() {

		given:
		def a = Anstellung.of(start_2019_12, stelle_e10_1, start_2019_12.plusYears(10), agz)

		when:
		def vorher = start_2019_12.minusMonths(1)
		a.am(vorher)

		then:
		def ex = thrown IllegalArgumentException
		ex.message == "Keine Stelle zu ${vorher} gefunden (frühest bekannte ist ${start_2019_12})"
	}

	def "Stelle kann nicht nach Ende hinzugefügt werden"() {

		given:
		def a = Anstellung.of(start_2019_12, stelle_e10_1, start_2019_12.plusYears(2), agz)

		when:
		a.add(start_2019_12.plusYears(3), stelle_e10_3)

		then:
		def ex = thrown IllegalArgumentException
		ex.message == "Stellenbeginn 2022-12 liegt nach dem Anstellungsende 2021-12"
	}

	def "Datum kann nicht doppelt hinzugefügt werden"() {

		given:
		def a = Anstellung.of(start_2019_12, stelle_e10_1, start_2019_12.plusYears(2), agz)

		when:
		a.add(start_2019_12.plusMonths(2), stelle_e10_1)
		a.add(start_2019_12.plusMonths(2), stelle_e10_3)

		then:
		def ex = thrown IllegalArgumentException
		ex.message == "Doppelter Stellenbeginn 2020-02 (alt: Stelle(E10/eins, 100%), neu: Stelle(E10/drei, 100%))"
	}

	def "einfache Anstellung"() {

		when:
		def a = Anstellung.of(start_2019_12, stelle_e10_1, start_2019_12.plusYears(10), agz)

		then:
		a.am(start_2019_12) == stelle_e10_1
		a.am(start_2019_12.plusMonths(11)) == stelle_e10_1
		a.am(start_2019_12.plusMonths(12)) == Stelle.of(E10, zwei)
		a.am(start_2019_12.plusMonths(13)) == Stelle.of(E10, zwei)
	}

	def "mit Weiterbeschäftigung & implizitem Aufstieg"() {

		when:
		def a = Anstellung.weiter(start_2019_12, stelle_e10_1,
				start_2019_12.plusMonths(monate), 65, start_2019_12.plusYears(2), agz)

		then:
		a.am(start_2019_12) == stelle_e10_1
		a.am(start_2019_12.plusMonths(monate-1)) == stelle_e10_1
		a.am(start_2019_12.plusMonths(monate)) == Stelle.of(E10, eins, 65)
		a.am(start_2019_12.plusMonths(11)) == Stelle.of(E10, eins, 65)
		a.am(start_2019_12.plusMonths(12)) == Stelle.of(E10, zwei, 65)

		where:
		monate << (3 .. 11)
	}

	def "mit Weiterbeschäftigung zum Stufenwechsel"() {

		when:
		def a = Anstellung.weiter(start_2019_12, stelle_e10_1,
				start_2019_12.plusMonths(12), 65, start_2019_12.plusYears(2), agz)

		then:
		a.am(start_2019_12) == stelle_e10_1
		a.am(start_2019_12.plusMonths(11)) == stelle_e10_1
		a.am(start_2019_12.plusMonths(12)) == Stelle.of(E10, zwei, 65)
	}

	def "mit Weiterbeschäftigung nach Aufstieg"() {

		when:
		def a = Anstellung.weiter(start_2019_12, stelle_e10_1,
				start_2019_12.plusMonths(13), 65, start_2019_12.plusYears(2), agz)

		then:
		a.am(start_2019_12) == stelle_e10_1
		a.am(start_2019_12.plusMonths(11)) == stelle_e10_1
		a.am(start_2019_12.plusMonths(12)) == Stelle.of(E10, zwei, 100)
		a.am(start_2019_12.plusMonths(13)) == Stelle.of(E10, zwei, 65)
	}

	def "mit Weiterbeschäftigung über Aufstiege (issue #46)"() {

		given:
		Anstellung weiter = Anstellung.weiter(
				YearMonth.of(2019, 2),
				Stelle.of(Gruppe.E13, Stufe.drei, 90),
				// -> stufe 4 ab 02/2022; stufe 5 ab 02/2026
				YearMonth.of(2022, 5), 55,
				YearMonth.of(2030, 12), agz)

		expect:
		weiter.am(ym) == Stelle.of(Gruppe.E13, expectedStufe, umfang)

		where:
		ym                     | expectedStufe | umfang
		// Aufstieg -> Stufe 4
		YearMonth.of(2022, 1)  | Stufe.drei  | 90
		YearMonth.of(2022, 2)  | Stufe.vier  | 90
		YearMonth.of(2022, 5)  | Stufe.vier  | 55
		// kein Aufstieg (issue #46)
		YearMonth.of(2023, 1)  | Stufe.vier  | 55
		YearMonth.of(2023, 2)  | Stufe.vier  | 55
		// Aufstieg -> Stufe 5
		YearMonth.of(2026, 1)  | Stufe.vier  | 55
		YearMonth.of(2026, 2)  | Stufe.fünf  | 55
	}

	def "bei stufe sechs ist schluß"() {

		given:
		def stelle_5 = Stelle.of(E10, fünf)

		when:
		def a = Anstellung.of(start_2019_12, stelle_5, start_2019_12.plusYears(30), agz)

		then:
		a.am(start_2019_12.plusYears(20)) == Stelle.of(E10, sechs)
	}

	def "calcBaseStellen happy path"() {

		when:
		def a = Anstellung.of(start_2019_12, stelle_e10_1, start_2019_12.plusYears(2), agz)

		then:
		a.calcBaseStellen(2020) == [
			stelle_e10_1,
			stelle_e10_1,
			stelle_e10_1
		]
	}

	def "calcBaseStellen happy path with Weiterschäftigung"() {

		when:
		def a = Anstellung.weiter(start_2019_12, stelle_e10_1,
				YearMonth.of(2020, 9), 65, start_2019_12.plusYears(2), agz)

		then:
		a.calcBaseStellen(2020) == [
			stelle_e10_1,
			stelle_e10_1,
			Stelle.of(E10, eins, 65)
		]
	}


	@Unroll
	def "months in year von #beginn bis #ende"() {

		given:
		Anstellung a = Anstellung.of(beginn, stelle_e10_1, ende, agz)

		expect:
		a.monthsInYear(2019) == resultMonthRange.collect {
			YearMonth.of(2019, it)
		}

		where:
		beginn                 | ende                   | resultMonthRange
		YearMonth.of(2018, 12) | YearMonth.of(2019, 7)  | (1..7)
		YearMonth.of(2019, 1)  | YearMonth.of(2019, 7)  | (1..7)
		YearMonth.of(2018, 12) | YearMonth.of(2020, 2)  | (1..12)
		YearMonth.of(2019, 1)  | YearMonth.of(2019, 12) | (1..12)
		YearMonth.of(2019, 3)  | YearMonth.of(2020, 2)  | (3..12)
		YearMonth.of(2019, 3)  | YearMonth.of(2019, 4)  | (3..4)
	}

	def "Monatsstellen über Weiterbeschäftigung"() {

		given:
		Anstellung weiter = Anstellung.weiter( YearMonth.of(2021, 1), Stelle.of(Gruppe.E13, Stufe.eins),
				YearMonth.of(2022, 1), 70, YearMonth.of(2023, 1), agz)

		def start = YearMonth.of(2021, 12)
		def end = YearMonth.of(2022, 1)

		when:
		def mk = weiter.monatsStellen(start, end)

		then:
		mk.size() == 2

		mk[start] == Stelle.of(Gruppe.E13, Stufe.eins)
		mk[end] == Stelle.of(Gruppe.E13, Stufe.zwei, 70)
	}

	@Unroll
	def "calcBaseStellen Randfall: Einstellung zu Monat #beginnMonth"() {

		expect:
		def start = YearMonth.of(2018, beginnMonth)
		def ans = Anstellung.of(start, stelle_e10_1, start.plusYears(2), agz)

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
		Stelle sZwei = Stelle.of(E10, zwei)

		when:
		def ans = Anstellung.of(start, stelle_e10_1, start.plusYears(2), agz)

		then:
		ans.calcBaseStellen(2019) == [stelle_e10_1, sZwei, sZwei]
	}
}
