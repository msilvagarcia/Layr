package layr.org.codehaus.jackson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

abstract class Converter<T> {
	abstract T convert( String value, Class<T> clazz ) throws IOException;
	
	@SuppressWarnings("unchecked")
	public Class<T> getGenericClass() {
		 return (Class<T>) 
			 ((ParameterizedType) getClass().getGenericSuperclass())
			 	.getActualTypeArguments()[0];
	}
}