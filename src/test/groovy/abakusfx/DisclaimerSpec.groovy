package abakusfx

import abakusfx.AppPrefs.PrefKeys
import fxTools.Windows

public class DisclaimerSpec extends AbstractAbakusSpec {

	void overrideAppPrefs() {
		prefsStore.put(PrefKeys.wasDisclaimerAccepted, "false")
	}

	def "accepting disclaimer opens main windows"() {

		when:
		def s = Windows.findFocusedStage()
		then:
		s.getTitle().contains("Nutzungsvereinbarung")

		when:
		click("Ja")
		then:
		prefsStore.get(PrefKeys.wasDisclaimerAccepted)
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
		prefsStore.get(PrefKeys.wasDisclaimerAccepted) != "true"
	}
}