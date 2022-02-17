package utils

import java.util.prefs.Preferences

import spock.lang.Specification

public class UserNodePrefsSpec extends Specification {

	Preferences prefs = Mock()

	enum Props {
		aProp, bProp
	}

	def "new prefs are empty"() {
		when:
		new UserNodePrefs<Props>(prefs).contains(Props.bProp)

		then:
		1 * prefs.get("bProp", null)
	}

	def "can put value"() {
		when:
		new UserNodePrefs<Props>(prefs).put(Props.bProp, "hello world")

		then:
		1 * prefs.put("bProp", "hello world")
	}

	def "can get value"() {
		when:
		prefs.get("bProp", null) >> "a value"
		def p = new UserNodePrefs<Props>(prefs)

		then:
		p.get(Props.bProp) == "a value"
	}

	def "can remove value"() {
		when:
		new UserNodePrefs<Props>(prefs).remove(Props.aProp)

		then:
		1 * prefs.remove("aProp")
	}
}
