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

	def "roundtrip for ProjectModel via YAML works"() {

		given:
		def m1 = new SeriesModel(LocalDate.of(2019, 2, 7), LocalDate.of(2020,5,11), //
				Gruppe.E13, Stufe.zwei, 80, true, LocalDate.of(2020,5,11))
		def m2 = new SeriesModel(LocalDate.of(2018, 1, 3), LocalDate.of(2021,11,13), //
				Gruppe.E10, Stufe.drei, 75, false, LocalDate.of(2019,4,12))
		def projModel = new ProjectModel(Arrays.asList(//
				new PersonModel("James Bond", m1),//
				new PersonModel("Amelia Earhart", m2)))

		when:
		def modelYaml = new ModelMapper().asString(projModel)
		def newModel = new ModelMapper().fromString(modelYaml, ProjectModel.class)

		then:
		newModel == projModel
	}
}