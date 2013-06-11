package layr.routing.lifecycle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import layr.api.Handler;

public class HandlerClassExtractor<T> {
	
	Map<String, Class<? extends T>> registeredHandlers;
	Class<T> interfaceClass;
	
	public HandlerClassExtractor(Class<T> interfaceClass) {
		this.registeredHandlers = new HashMap<String, Class<? extends T>>();
		this.interfaceClass = interfaceClass;
	}
	
	public static <T> HandlerClassExtractor<T> newInstance( Class<T> interfaceClass ){
		return new HandlerClassExtractor<T>( interfaceClass );
	}

	public void extract(Class<?> clazz) {
		if ( !clazz.isAnnotationPresent( Handler.class )
		||   !interfaceClass.isAssignableFrom( clazz ))
			return;

		for ( Type type : clazz.getGenericInterfaces() )
			extract( clazz, type );
	}

	@SuppressWarnings("unchecked")
	public void extract(Class<?> clazz, Type type ) {
		if ( !ParameterizedType.class.isInstance( type ) )
			return;
		
		ParameterizedType ptype = (ParameterizedType)type;
		if ( interfaceClass.equals( ptype.getRawType() ) ){
			Class<?> exceptionClass = (Class<?>)ptype.getActualTypeArguments()[0];
			registeredHandlers.put( exceptionClass.getCanonicalName(), (Class<T>) clazz );
		}
	}

	public Map<String, Class<? extends T>> getRegisteredHandlers() {
		return registeredHandlers;
	}
}
