package abakusfx.models;

import static abakus.Constants.eq;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import abakus.Gruppe;
import abakus.Stufe;

public class SeriesModel {

	public final LocalDate von;
	public final LocalDate bis;
	public final Gruppe gruppe;
	public final Stufe stufe;
	public final int umfang;
	public final boolean isWeiter;
	public final LocalDate seit;
	public final int umfangSeit;

	@JsonCreator
	public SeriesModel(@JsonProperty("von") final LocalDate von, @JsonProperty("bis") final LocalDate bis,
			@JsonProperty("gruppe") final Gruppe gruppe, @JsonProperty("stufe") final Stufe stufe,
			@JsonProperty("umfang") final int umfang, @JsonProperty("isWeiter") final boolean isWeiter,
			@JsonProperty("seit") final LocalDate seit, @JsonProperty("umfangSeit") final int umfangSeit) {
		this.von = von;
		this.bis = bis;
		this.gruppe = gruppe;
		this.stufe = stufe;
		this.umfang = umfang;
		this.isWeiter = isWeiter;
		this.seit = seit;
		this.umfangSeit = umfangSeit;
	}

	@Override
	public int hashCode() {
		return Objects.hash(von, bis, gruppe, stufe, umfang, isWeiter, seit, umfangSeit);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		final SeriesModel other = (SeriesModel) obj;

		return eq(von, other.von) && eq(bis, other.bis) && //
				gruppe == other.gruppe && stufe == other.stufe && //
				eq(umfang, other.umfang) && isWeiter == other.isWeiter && //
				eq(seit, other.seit) && eq(umfangSeit, other.umfangSeit);
	}

	public static SeriesModel fallback() {
		return new SeriesModel(LocalDate.now(), LocalDate.now().plusMonths(3), Gruppe.E10, Stufe.eins, 100, false,
				LocalDate.now().minusMonths(6), 100);
	}
}
