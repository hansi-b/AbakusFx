package abakus;

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
}
