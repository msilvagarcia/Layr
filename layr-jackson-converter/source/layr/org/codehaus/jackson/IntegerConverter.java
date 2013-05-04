package layr.org.codehaus.jackson;

import java.io.IOException;

public class IntegerConverter extends Converter<Integer> {

	@Override
	public Integer convert(String value, Class<Integer> clazz) throws IOException {
		return Integer.valueOf( value );
	}

}
