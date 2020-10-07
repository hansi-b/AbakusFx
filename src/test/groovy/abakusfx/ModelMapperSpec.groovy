package abakusfx

import java.time.LocalDate

import abakus.Gruppe
import abakus.Stufe
import spock.lang.Specification

public class ModelMapperSpec extends Specification {

	def "roundtrip for SeriesModel via YAML works"() {

		given:
		def model = new SeriesModel(LocalDate.of(2019, 2, 7), LocalDate.of(2020,5,11), //
				Gruppe.E13, Stufe.zwei, 80, true, LocalDate.of(2020,5,11))

		when:
		def modelYaml = new ModelMapper().asString(model)
		def newModel = new ModelMapper().fromString(modelYaml, SeriesModel.class)

		then:
		newModel == model
	}
}