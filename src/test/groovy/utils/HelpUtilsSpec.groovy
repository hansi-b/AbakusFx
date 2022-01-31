package utils;

import spock.lang.Specification

public class HelpUtilsSpec extends Specification {

	def "simple help"() {
		expect:
		HelpUtils.csvTarifToHtmlTable("""# alpha	yes	3rd
abc	2019	202.3
xyz	2012	11.7
""") == """<table>
<tr><th>alpha</th><th>yes</th><th>3rd</th></tr>
<tr><td>abc</td><td>2019</td><td>202.3</td></tr>
<tr><td>xyz</td><td>2012</td><td>11.7</td></tr>
</table>
"""
	}

	def "missing header"() {
		when:
		HelpUtils.csvTarifToHtmlTable(""" alpha	yes	3rd
abc	2019	202.3
""")
		then:
		thrown IllegalStateException
	}

	def "data before header"() {
		when:
		HelpUtils.csvTarifToHtmlTable("""# alpha	yes	3rd
# abc	2019	202.3
abc	2019	202.3
""")
		then:
		thrown IllegalStateException
	}

	def "no data nor header"() {
		when:
		HelpUtils.csvTarifToHtmlTable("""
""")
		then:
		thrown IllegalStateException
	}
}
