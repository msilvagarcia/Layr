package layr.org.codehaus.jackson;

import java.io.IOException;

public class ShortConverter extends Converter<Short> {

	@Override
	public Short convert(String value, Class<Short> clazz) throws IOException {
		return Short.valueOf( value );
	}

}
