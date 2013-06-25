package layr.routing.converter;

import layr.exceptions.ConversionException;


public class DoubleConverter extends Converter<Double> {

	@Override
	public Double convert(String value, Class<Double> clazz) throws ConversionException {
		return Double.valueOf( value );
	}

}
