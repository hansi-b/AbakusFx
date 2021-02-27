package abakusfx.models;

import static abakus.Constants.eq;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectModel {
	public final List<PersonModel> persons;

	@JsonCreator
	public ProjectModel(@JsonProperty("persons") final List<PersonModel> persons) {
		this.persons = Collections.unmodifiableList(persons);
	}

	@Override
	public int hashCode() {
		return persons.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		return eq(persons, ((ProjectModel) obj).persons);
	}
}
