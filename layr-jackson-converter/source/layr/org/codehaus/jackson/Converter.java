package layr.org.codehaus.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;

abstract class Converter<T> {

	abstract T convert( String value, Class<T> clazz ) throws IOException;

	T convert( InputStream value, Class<T> clazz ) throws IOException {
		throw new IOException("Impossible to convert data from InputStream.");
	}

	@SuppressWarnings("unchecked")
	public Class<T> getGenericClass() {
		 return (Class<T>) 
			 ((ParameterizedType) getClass().getGenericSuperclass())
			 	.getActualTypeArguments()[0];
	}
}