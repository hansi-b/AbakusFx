package abakusfx

import spock.lang.Specification

public class ProjectTabsControllerSpec extends Specification {

	def "can fall back and load ProjectModel from SeriesModel"() {
		given:
		def series = '''---
von: "2019-02-07"
bis: "2020-05-11"
gruppe: "E13"
stufe: "zwei"
umfang: 45
isWeiter: true
seit: "2020-05-11"
umfangSeit: 100
'''

		when:
		def proj = ProjectTabsController.loadModel(series)

		then:
		ProjectModel.isCase(proj)
		proj.persons.size() == 1
		proj.persons.get(0).name == "NN"
		proj.persons.get(0).series.umfang == 45
	}
}
