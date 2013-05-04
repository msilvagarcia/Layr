package layr.org.codehaus.jackson;

import java.io.IOException;

public class ByteConverter extends Converter<Byte> {

	@Override
	public Byte convert(String value, Class<Byte> clazz) throws IOException {
		return Byte.valueOf( value );
	}

}
