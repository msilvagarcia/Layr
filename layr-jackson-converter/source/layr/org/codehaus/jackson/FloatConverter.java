package layr.org.codehaus.jackson;

import java.io.IOException;

public class FloatConverter extends Converter<Float> {

	@Override
	public Float convert(String value, Class<Float> clazz) throws IOException {
		return Float.valueOf( value );
	}

}
