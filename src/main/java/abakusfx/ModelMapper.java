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
package abakusfx;

import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ModelMapper {

	private final ObjectMapper objectMapper;

	public ModelMapper() {
		objectMapper = createObjectMapper();
	}

	private static ObjectMapper createObjectMapper() {
		final ObjectMapper om = new ObjectMapper(new YAMLFactory());
		final SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, "", "", ""));
		testModule.addSerializer(LocalDate.class, new LocalDateSerializer());
		testModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
		om.registerModule(testModule);
		return om;
	}

	public <T> T fromString(final String yaml, final Class<T> clazz) throws JsonProcessingException {
		return objectMapper.readValue(yaml, clazz);
	}

	public <T> String asString(final T model) throws JsonProcessingException {
		return objectMapper.writeValueAsString(model);
	}

	private static class LocalDateSerializer extends JsonSerializer<LocalDate> {
		@Override
		public void serialize(final LocalDate value, final JsonGenerator gen, final SerializerProvider serializers)
				throws IOException {
			gen.writeString(value.toString());
		}
	}

	private static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
		@Override
		public LocalDate deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
			return LocalDate.parse(p.getValueAsString());
		}
	}
}
