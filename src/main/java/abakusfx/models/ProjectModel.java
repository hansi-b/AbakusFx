/**
 * Abakus - https://github.com/hansi-b/AbakusFx
 *
 * Copyright (C) 2021  Hans Bering
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
