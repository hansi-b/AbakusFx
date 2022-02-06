package abakusfx

import abakusfx.AppPrefs.PrefKeys
import fxTools.Windows

public class DisclaimerSpec extends AbstractAbakusSpec {

	void overrideAppPrefs() {
		prefs.put(PrefKeys.wasDisclaimerAccepted, "false")
	}

	def "accepting disclaimer opens main windows"() {

		when:
		def s = Windows.findFocusedStage()
		then:
		s.getTitle().contains("Nutzungsvereinbarung")

		when:
		click("Ja")
		then:
		prefs.get(PrefKeys.wasDisclaimerAccepted)
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
		prefs.get(PrefKeys.wasDisclaimerAccepted) != "true"
	}
}