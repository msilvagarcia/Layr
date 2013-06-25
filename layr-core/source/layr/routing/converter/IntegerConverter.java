package layr.routing.converter;

import layr.exceptions.ConversionException;


public class IntegerConverter extends Converter<Integer> {

	@Override
	public Integer convert(String value, Class<Integer> clazz) throws ConversionException {
		return Integer.valueOf( value );
	}

}
