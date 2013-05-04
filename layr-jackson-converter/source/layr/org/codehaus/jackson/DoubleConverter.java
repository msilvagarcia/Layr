package layr.org.codehaus.jackson;

import java.io.IOException;

public class DoubleConverter extends Converter<Double> {

	@Override
	public Double convert(String value, Class<Double> clazz) throws IOException {
		return Double.valueOf( value );
	}

}
