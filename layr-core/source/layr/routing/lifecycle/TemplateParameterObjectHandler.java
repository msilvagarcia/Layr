package layr.routing.lifecycle;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import layr.api.RequestContext;


public class TemplateParameterObjectHandler {

	RequestContext requestContext;

	public TemplateParameterObjectHandler( RequestContext requestContext ) {
		this.requestContext = requestContext;
	}

	public void memorizeParameters(Object instance) {
			if ( instance == null )
				return;

			Class<?> clazz = instance.getClass();
			while (!Object.class.equals(clazz) ){
				for ( Field field : clazz.getDeclaredFields() )
					memorizeFieldAsParameter( instance, field );
				clazz = clazz.getSuperclass();
			}
	}

	public void memorizeFieldAsParameter(Object instance, Field field) {
		Object value;
		if ( isPublic(field) )
			value = getAttributeFromField(instance, field);
		else
			value = getAttributeFromGetterMethod(instance, field);
		requestContext.put(field.getName(), value);
	}

	public Object getAttributeFromField(Object instance, Field field) {
		try {
			field.setAccessible(true);
			Object value = field.get( instance );
			return value;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getAttributeFromGetterMethod(Object instance, Field field) {
		try {
			Method getter = extractGetterFor(instance, field.getName());
			if ( getter == null || !isPublic(getter) )
				return null;
			getter.setAccessible(true);
			return getter.invoke(instance);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method extractGetterFor(Object target, String attribute)
			throws SecurityException {
		String getter = String.format("get%s%s", attribute.substring(0, 1)
				.toUpperCase(), attribute.substring(1));
		try {
			return target.getClass().getMethod(getter);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	public boolean isPublic( Method method ) {
		return isPublic( method.getModifiers() );
	}
	
	public boolean isPublic ( Field field ) {
		return isPublic(field.getModifiers());
	}

	public boolean isPublic(int modifiers) {
		return modifiers == Member.DECLARED
			|| modifiers == Member.PUBLIC;
	}
}
