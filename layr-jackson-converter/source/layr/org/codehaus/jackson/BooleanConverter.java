package layr.org.codehaus.jackson;

import java.io.IOException;

public class BooleanConverter extends Converter<Boolean> {

	@Override
	public Boolean convert(String value, Class<Boolean> clazz) throws IOException {
		return Boolean.valueOf( value );
	}

}
