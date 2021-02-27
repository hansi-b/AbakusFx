package abakusfx.models;

import static abakus.Constants.eq;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonModel {
	public final String name;
	public final SeriesModel series;

	public PersonModel(@JsonProperty("name") final String name, @JsonProperty("series") final SeriesModel series) {
		this.name = name;
		this.series = series;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, series);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		final PersonModel other = (PersonModel) obj;
		return eq(name, other.name) && eq(series, other.series);
	}
}