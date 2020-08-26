package abakusFx;

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

    public ModelMapper() {
        objectMapper = createObjectMapper();
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, "", "", ""));
        testModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        testModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        om.registerModule(testModule);
        return om;
    }

    public <T> T fromString(String yaml, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(yaml, clazz);
    }

    public <T> String asString(T model) throws JsonProcessingException {
        return objectMapper.writeValueAsString(model);
    }

    private final ObjectMapper objectMapper;

    private static class LocalDateSerializer extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString());
        }
    }

    private static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LocalDate.parse(p.getValueAsString());
        }
    }
}
