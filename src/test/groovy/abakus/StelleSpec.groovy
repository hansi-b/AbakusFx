package abakus

import spock.lang.Specification
import static abakus.Gruppe.*
import static abakus.Stufe.*

public class StelleSpec extends Specification {

	def "vollzeit wird richtig erkannt"() {

		expect:
		Stelle.of(E13, sechs).istVollzeit() == true
		Stelle.of(E13, sechs, 95).istVollzeit() == false
		Stelle.of(E13, sechs, 100).istVollzeit() == true
	}
	
	def "equality checks"() {

		expect:
		def s = Stelle.of(E13, sechs)
		
		s.equals(s)
		!s.equals(null)
		
		!s.equals(Stelle.of(E10, sechs))
		!s.equals(Stelle.of(E10, vier))
		s.equals(Stelle.of(E13, sechs))
	}
}
