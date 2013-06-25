package layr.routing.converter;

import java.io.InputStream;

import layr.api.InputConverter;
import layr.exceptions.ConversionException;

public class DefaultInputConverter implements InputConverter {

	private ConverterFactory converterFactory;
	
	public DefaultInputConverter() {
		converterFactory = new ConverterFactory();
	}

	@Override
	public <T> T convert(String value, Class<T> targetClass) throws ConversionException {
		return converterFactory.decode(value, targetClass);
	}

	@Override
	public <T> T convert(InputStream value, Class<T> targetClass)
			throws ConversionException {
		throw new ConversionException("There's no InputConverter configured to this type of request.");
	}
}
