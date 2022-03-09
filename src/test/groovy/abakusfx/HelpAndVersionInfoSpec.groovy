package abakusfx

import abakusfx.AppPrefs.PrefKeys
import fxTools.Windows

public class HelpAndVersionInfoSpec extends AbstractAbakusSpec {

	def "can open and close version info"() {

		given:
		click('Hilfe')
		click('Über...')

		when:
		def s = Windows.findFocusedStage()

		then:
		s.getTitle().contains("Version")

		when:
		click('OK')

		then:
		APP_SPEC_WIDOW_TITLE.equals(Windows.findFocusedStage().getTitle())
	}

	def "can open and close help"() {

		given:
		click('Hilfe')
		click('Hilfe anzeigen...')

		when:
		def s = Windows.findFocusedStage()

		then:
		s.getTitle().contains("Hilfe")

		when:
		interact(()->(s.getScene().getWindow()).close());
		def t = Windows.findFocusedStage()

		then:
		t != null
		APP_SPEC_WIDOW_TITLE.equals(t.getTitle())
	}
}