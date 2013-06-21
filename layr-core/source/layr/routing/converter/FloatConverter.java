package layr.routing.converter;

import layr.exceptions.ConversionException;


public class FloatConverter extends Converter<Float> {

	@Override
	public Float convert(String value, Class<Float> clazz) throws ConversionException {
		return Float.valueOf( value );
	}

}
