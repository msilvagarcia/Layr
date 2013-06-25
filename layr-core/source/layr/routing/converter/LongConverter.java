package layr.routing.converter;

import layr.exceptions.ConversionException;


public class LongConverter extends Converter<Long> {

	@Override
	public Long convert(String value, Class<Long> clazz) throws ConversionException {
		return Long.valueOf( value );
	}

}
