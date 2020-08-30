package abakusFx

import abakus.Gruppe
import abakus.Stufe
import groovy.transform.Immutable

import java.time.LocalDate

@Immutable
class SeriesModel {

    LocalDate von
    LocalDate bis
    Gruppe gruppe
    Stufe stufe
    Integer umfang
    Boolean isWeiter
    LocalDate seit
    Integer umfangSeit

    static SeriesModel of(SerieSettingsController ssc) {
        new SeriesModel(
                von: ssc.von.getValue(),
                bis: ssc.bis.getValue(),
                gruppe: ssc.gruppe.getValue(),
                stufe: ssc.stufe.getValue(),
                umfang: ssc.umfang.getValue(),
                isWeiter: ssc.weiter.isSelected(),
                seit: ssc.seit.getValue(),
        //        umfangSeit: ssc.umfangSeit.getValue()
        )
    }


    static SeriesModel fallback() {
        new SeriesModel(
                von: LocalDate.now(),
                bis: LocalDate.now().plusMonths(3),
                gruppe: Gruppe.E10,
                stufe: Stufe.eins,
                umfang: 100,
                isWeiter: false,
                seit: LocalDate.now().minusMonths(6),
          //      umfangSeit: 100
        )
    }
}
