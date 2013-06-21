package layr.routing.converter;

import layr.exceptions.ConversionException;


public class ByteConverter extends Converter<Byte> {

	@Override
	public Byte convert(String value, Class<Byte> clazz) throws ConversionException {
		return Byte.valueOf( value );
	}

}
