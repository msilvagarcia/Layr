package layr.org.codehaus.jackson;

import java.io.IOException;

public class LongConverter extends Converter<Long> {

	@Override
	public Long convert(String value, Class<Long> clazz) throws IOException {
		return Long.valueOf( value );
	}

}
