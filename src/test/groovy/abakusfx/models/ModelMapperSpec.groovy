package abakusfx.models

import java.time.LocalDate

import abakus.Gruppe
import abakus.Stufe
import abakusfx.ModelMapper
import abakusfx.models.PersonModel
import abakusfx.models.ProjectModel
import abakusfx.models.SeriesModel
import spock.lang.Specification

public class ModelMapperSpec extends Specification {

	def "roundtrip for SeriesModel via YAML works"() {

		given:
		def model = new SeriesModel(LocalDate.of(2019, 2, 7), LocalDate.of(2020,5,11), //
				Gruppe.E13, Stufe.zwei, 80, true, LocalDate.of(2020,5,11), 80, 20.0)

		when:
		def modelYaml = new ModelMapper().asString(model)
		def newModel = new ModelMapper().fromString(modelYaml, SeriesModel.class)

		then:
		newModel == model
	}

	def "roundtrip for ProjectModel via YAML works"() {

		given:
		def m1 = new SeriesModel(LocalDate.of(2019, 2, 7), LocalDate.of(2020,5,11), //
				Gruppe.E13, Stufe.zwei, 80, true, LocalDate.of(2020,5,11), 90, 23.0)
		def m2 = new SeriesModel(LocalDate.of(2018, 1, 3), LocalDate.of(2021,11,13), //
				Gruppe.E10, Stufe.drei, 75, false, LocalDate.of(2019,4,12), 90, 30.0)
		def projModel = new ProjectModel(Arrays.asList(//
				new PersonModel("James Bond", m1),//
				new PersonModel("Amelia Earhart", m2)))

		when:
		def modelYaml = new ModelMapper().asString(projModel)
		def newModel = new ModelMapper().fromString(modelYaml, ProjectModel.class)

		then:
		newModel == projModel
	}

	def "missing agz in yaml defaults to 30"() {
		given:
		def duffyYaml = """
von: "2018-01-03"
bis: "2021-11-13"
gruppe: "E8"
stufe: "zwei"
umfang: 50
isWeiter: false
seit: "2019-04-12"
umfangSeit: 70
"""
		when:
		def duffy = new ModelMapper().fromString(duffyYaml, SeriesModel.class)

		then:
		duffy.agz == 30.0
	}
}