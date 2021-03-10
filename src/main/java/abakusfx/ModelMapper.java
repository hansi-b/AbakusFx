package abakusfx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.time.LocalDate;

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
