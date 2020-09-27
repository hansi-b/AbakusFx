package abakusFx;

import java.time.LocalDate;

import abakus.Gruppe;
import abakus.Stufe;

class SeriesModel {

	final LocalDate von;
	final LocalDate bis;
	final Gruppe gruppe;
	final Stufe stufe;
	final Integer umfang;
	final Boolean isWeiter;
	final LocalDate seit;
	final Integer umfangSeit = 100;

	public SeriesModel(final LocalDate von, final LocalDate bis, final Gruppe gruppe, final Stufe stufe,
			final Integer umfang, final Boolean isWeiter, final LocalDate seit) {
		this.von = von;
		this.bis = bis;
		this.gruppe = gruppe;
		this.stufe = stufe;
		this.umfang = umfang;
		this.isWeiter = isWeiter;
		this.seit = seit;
//		this.umfangSeit = umfangSeit;
	}

	static SeriesModel of(final SerieSettingsController ssc) {
		return new SeriesModel(ssc.von.getValue(), ssc.bis.getValue(), ssc.gruppe.getValue(), ssc.stufe.getValue(),
				ssc.umfang.getValue(), ssc.weiter.isSelected(), ssc.seit.getValue()
		// umfangSeit: ssc.umfangSeit.getValue()
		);
	}

	static SeriesModel fallback() {
		return new SeriesModel(LocalDate.now(), LocalDate.now().plusMonths(3), Gruppe.E10, Stufe.eins, 100, false,
				LocalDate.now().minusMonths(6)
		// umfangSeit: 100
		);
	}
}
