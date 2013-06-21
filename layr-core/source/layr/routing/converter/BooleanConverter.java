package layr.routing.converter;

import layr.exceptions.ConversionException;


public class BooleanConverter extends Converter<Boolean> {

	@Override
	public Boolean convert(String value, Class<Boolean> clazz) throws ConversionException {
		return Boolean.valueOf( value );
	}

}
