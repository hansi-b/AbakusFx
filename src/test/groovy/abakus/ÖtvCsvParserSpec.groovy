package abakus

import spock.lang.Specification

public class ÖtvCsvParserSpec extends Specification {

	def "Exception für doppelte Kombo Jahr + Gruppe"() {
		given:
		String csv = """
2020-01	E8	89,4	1,53	2,04	3,79	4,44	5,35	6,15
2020-01	E8	88,14	10,21	20,04	30,79	40,44	50,35	60,15
"""
		when:
		def tarif = new ÖtvCsvParser().parseGehälter(new ByteArrayInputStream(csv.getBytes( 'UTF-8' )))

		then:
		def e = thrown(IllegalArgumentException)
		e.message == "Doppelte Daten für Gruppe E8 ab 2020-01"
	}
}
