package layr.api;

import java.io.InputStream;

import layr.exceptions.ConversionException;

public interface InputConverter {

	<T> T convert(String value, Class<T> targetClass) throws ConversionException;

	<T> T convert(InputStream value, Class<T> targetClass) throws ConversionException;

}
