package abakusfx;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import abakus.Gruppe;
import abakus.Stufe;

class SeriesModel {

	public final LocalDate von;
	public final LocalDate bis;
	public final Gruppe gruppe;
	public final Stufe stufe;
	public final int umfang;
	public final boolean isWeiter;
	public final LocalDate seit;
	public final int umfangSeit = 100;

	@JsonCreator
	public SeriesModel(@JsonProperty("von") final LocalDate von, @JsonProperty("bis") final LocalDate bis,
			@JsonProperty("gruppe") final Gruppe gruppe, @JsonProperty("stufe") final Stufe stufe,
			@JsonProperty("umfang") final Integer umfang, @JsonProperty("isWeiter") final Boolean isWeiter,
			@JsonProperty("seit") final LocalDate seit) {
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
