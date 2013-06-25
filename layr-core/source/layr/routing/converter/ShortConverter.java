package layr.routing.converter;

import layr.exceptions.ConversionException;


public class ShortConverter extends Converter<Short> {

	@Override
	public Short convert(String value, Class<Short> clazz) throws ConversionException {
		return Short.valueOf( value );
	}

}
