package abakusfx

import org.hansib.sundries.fx.Windows

import abakusfx.AppPrefs.PrefKeys

public class DisclaimerSpec extends AbstractAbakusSpec {

	void overrideAppPrefs() {
		prefsStore.put(PrefKeys.wasDisclaimerAccepted.name(), 'false')
	}

	def "accepting disclaimer opens main windows"() {

		when:
		def s = Windows.findFocusedStage()
		then:
		s.getTitle().contains("Nutzungsvereinbarung")

		when:
		click("Ja")
		then:
		appPrefs.disclaimerAccepted().isTrue()
		APP_SPEC_WIDOW_TITLE.equals(Windows.findFocusedStage().getTitle())
	}

	def "rejecting disclaimer closes app"() {

		when:
		def s = Windows.findFocusedStage()
		then:
		s.getTitle().contains("Nutzungsvereinbarung")

		when:
		click("Nein")
		then:
		appPrefs.disclaimerAccepted().isFalse()
	}
}