package abakusfx;

import java.util.function.Consumer

import spock.lang.Specification

public class AppTitleSpec extends Specification {

	def titleHandler = Mock(Consumer)
	def "project name updates title"() {

		given:
		def a = new AppTitle(titleHandler)

		when:
		a.updateProject("SomeName.aba")

		then:
		1 * titleHandler.accept("Abakus: SomeName")

		when:
		a.updateProject("AnotherThing")

		then:
		1 * titleHandler.accept("Abakus: AnotherThing")
	}

	def "dirty state updates title"() {

		given:
		def a = new AppTitle(titleHandler)

		when:
		a.updateIsDirty(true)

		then:
		1 * titleHandler.accept("Abakus*")

		when:
		a.updateIsDirty(false)

		then:
		1 * titleHandler.accept("Abakus")
	}
}
