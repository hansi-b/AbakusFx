package abakusfx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class ResourceLoader {

	static final ResourceLoader loader = new ResourceLoader();

	String resourceAsString(final String resourceName) throws IOException {
		try (InputStream resStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
			return new String(resStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}