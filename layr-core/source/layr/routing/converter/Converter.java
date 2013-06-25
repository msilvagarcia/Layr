package layr.routing.converter;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;

import layr.exceptions.ConversionException;

public abstract class Converter<T> {

	public abstract T convert( String value, Class<T> clazz ) throws ConversionException;

	public T convert( InputStream value, Class<T> clazz ) throws ConversionException {
		throw new ConversionException("Impossible to convert data from InputStream.");
	}

	@SuppressWarnings("unchecked")
	public Class<T> getGenericClass() {
		 return (Class<T>) 
			 ((ParameterizedType) getClass().getGenericSuperclass())
			 	.getActualTypeArguments()[0];
	}
}