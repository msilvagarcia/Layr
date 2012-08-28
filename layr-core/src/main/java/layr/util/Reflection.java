/**
 * Copyright 2012 Miere Liniel Teixeira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package layr.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import layr.annotation.Converter;


public class Reflection {

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static Object getAttribute ( Object target, String attribute ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getter = extractGetterFor(target, attribute);
		if (getter != null)
			return getter.invoke(target);

		Field field = extractFieldFor(target.getClass(), attribute);

		if ( field == null )
			return null;

		field.setAccessible(true);
		return field.get(target);
	}
	
	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public static String getAttributeAndEncodeReturnedValue ( Object target, String attribute ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Field field = extractFieldFor(target, attribute);
		Class<?> type = field.getType();
		Converter annotation = field.getAnnotation(Converter.class);
		Object object = getAttribute(target, attribute);

		if (!String.class.equals(type) || annotation != null) {
			IConverter converter = (annotation != null)
					? annotation.value().newInstance() : new DefaultDataParser();
			object = converter.encode(object);
		}

		return (String) object;
	}

	/**
	 * Retrieves the attribute from object. If it returns null, it will create an empty
	 * instance from its attribute and return it.
	 * 
	 * @param target
	 * @param attribute
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws InstantiationException 
	 * 		When using inner classes see: http://stackoverflow.com/a/2098009/548685
	 */
	public static Object getOrCreateEmptyAttribute ( Object target, String attribute )
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
				   SecurityException, NoSuchFieldException, InstantiationException {
		Object object = getAttribute(target, attribute);
		if ( object != null )
			return object;
		
		Class<?> returnType = extractReturnTypeFor(target, attribute);
		// FIXME: nowadays, it doesn't supports Inner Classes at all
		object = returnType.newInstance();
		setAttribute(target, attribute, object);
		
		return object;
	}

	/**
	 * @param target
	 * @param attribute
	 * @param value
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void setAttribute ( Object target, String attribute, Object value ) throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method setter = extractSetterFor(target, attribute, value.getClass());
		if ( setter != null ) {
			setter.invoke(target, value);
			return;
		}

		Field field = extractFieldFor(target.getClass(), attribute);
		if ( field == null )
			return;

		field.setAccessible(true);
		field.set(target, value);
	}

	/**
	 * @param target
	 * @param attribute
	 * @param value
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public static void decodeValueAndSetAttribute(Object target, String attribute, Object value)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		
		Field field = extractFieldFor(target, attribute);
		Class<?> type = field.getType();
		Converter annotation = field.getAnnotation(Converter.class);
		Object parsedValue = value;
		
		if (!String.class.equals(type) || annotation != null) {
			IConverter converter = (annotation != null) ? annotation.value().newInstance() : new DefaultDataParser();
			
			parsedValue =  converter.decode(
					parsedValue, type,
					Reflection.extractGenericReturnTypeFor(field)
				);
		}

		Reflection.setAttribute(target, attribute, parsedValue);
	}
	
	/**
	 * @param annotation
	 * @param target
	 * @return
	 */
	public static List<Field>  extractAnnotatedFieldsFor(Class<? extends Annotation> annotation, Object target) {
		ArrayList<Field> fields = new ArrayList<Field>();

		Class<? extends Object> clazz = target.getClass();
		while (!clazz.equals(Object.class)) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation))
					fields.add(field);
			}
			clazz = clazz.getSuperclass();
		}

		return fields;
	}
        
        public static List<Method> extractAnnotatedMethodsFor(Class<? extends Annotation> annotation, Object target) {
            ArrayList<Method> methods = new ArrayList<Method>();
            
            Class<? extends Object> clazz = target.getClass();
            while (!clazz.equals(Object.class)) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(annotation))
					methods.add(method);
			}
			clazz = clazz.getSuperclass();
		}
            
            return methods;
        }

	/**
	 * @param attribute
	 * @param target
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static Annotation[] extractAnnotationsFor(String attribute, Object target)
			throws SecurityException, NoSuchFieldException {
		Field field = extractFieldFor(target, attribute);

		if (field == null)
			return new Annotation[]{};

		return field.getAnnotations();
	}

	/**
	 * @param attribute
	 * @param target
	 * @param annotation
	 * @return
	 * @throws SecurityException
	 */
	public static Annotation extractAnnotationFor(String attribute, Object target, Class<? extends Annotation> annotation)
			throws SecurityException {
		Field field = extractFieldFor(target, attribute);

		if (field == null)
			return null;

		return field.getAnnotation(annotation);
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 */
	public static Field extractFieldFor (Object target, String attribute) {
		Class<? extends Object> clazz = target.getClass();
		return extractFieldFor(clazz, attribute);
	}

	/**
	 * @param clazz
	 * @param attribute
	 * @return
	 */
	public static Field extractFieldFor (Class<?> clazz, String attribute) {
		try {
			if (clazz.equals(Object.class))
				return null;
			return clazz.getDeclaredField(attribute);
		} catch (NoSuchFieldException e) {
			return extractFieldFor(clazz.getSuperclass(), attribute);
		}
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static Class<?> extractReturnTypeFor (Object target, String attribute) 
			throws SecurityException {
		Field field = extractFieldFor(target, attribute);
		if (field == null)
			return null;
		return field.getType();
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws SecurityException
	 */
	public static Type[] extractGenericReturnTypeFor(Object target, String attribute)
			throws SecurityException {
		Field field = extractFieldFor(target, attribute);
		return extractGenericReturnTypeFor ( field );
	}
	
	/**
	 * @param field
	 * @return
	 */
	public static Type[] extractGenericReturnTypeFor(Field field) {
		if (field == null)
			return new Type[]{};
		Type type = field.getGenericType();
		if (!(type instanceof ParameterizedType))
			return new Type[]{};
		ParameterizedType ptype = (ParameterizedType) type;
		return ptype.getActualTypeArguments();
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public static Method extractGetterFor(Object target, String attribute)
			throws SecurityException {
		String getter = String.format("get%s%s",
				attribute.substring(0, 1).toUpperCase(),
				attribute.substring(1));
		try {
			return target.getClass().getMethod(getter);
		} catch (NoSuchMethodException e) {
			try {
				return target.getClass().getMethod(getter.replaceFirst("get", "is"));
			} catch (NoSuchMethodException e1) {
				return null;
			}
		}
	}
	
	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static Method extractSetterFor(Object target, String attribute) throws SecurityException, NoSuchFieldException {
		return extractSetterFor(target, attribute,
				extractReturnTypeFor(target, attribute));
	}

	/**
	 * @param target
	 * @param attribute
	 * @param returnTypeFor
	 * @return
	 * @throws SecurityException
	 */
	public static Method extractSetterFor(Object target, String attribute, Class<?> returnTypeFor) throws SecurityException {
		String setter = String.format("set%s%s",
				attribute.substring(0, 1).toUpperCase(),
				attribute.substring(1));
		try {
			return target.getClass().getMethod(setter, returnTypeFor);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * Instantiate the className
	 * @param className
	 * @return
	 */
	public static Object instanceForName (String className) {
		try {
			Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			return clazz.newInstance();
		} catch (Exception e) {
			return null;
		}
	}
}
