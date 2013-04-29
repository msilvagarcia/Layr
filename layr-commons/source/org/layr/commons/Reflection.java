/**
 * Copyright 2013 Miere Liniel Teixeira
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
package org.layr.commons;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Reflection {

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws NoSuchFieldException
	 */
	public static Object getAttribute(Object target, String attribute) {
		if ( target == null )
			return null;

		String[] stripedAttribute = stripAttribute(attribute);
		if (stripedAttribute.length == 1)
			return tryGetAttributeByReflection(target, attribute);

		Object object = tryGetAttributeByReflection(target, stripedAttribute[0]);
		return getAttribute(object, stripedAttribute[1]);
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public static Object tryGetAttributeByReflection( Object target, String attribute ) {
		try {
			Method getter = extractGetterFor(target, attribute);
			if (getter != null){
				getter.setAccessible( true );
				return getter.invoke(target);
			}
	
			Field field = extractFieldFor(target.getClass(), attribute);
			field.setAccessible(true);
			return field.get(target);
		} catch ( Throwable e ) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param attribute
	 * @return
	 */
	public static String[] stripAttribute(String attribute) {
		if (!attribute.contains("."))
			return new String[] { attribute };
		return attribute.split("\\.", 2);
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
	 * @throws InstantiationException
	 */
	public static void setAttribute(Object target, String attribute,
			Object value) throws SecurityException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchFieldException, InstantiationException
	{
		String[] stripedAttribute = stripAttribute(attribute);
		if (stripedAttribute.length == 1) {
			tryToSetAttributeByReflection(target, attribute, value);
			return;
		}

		String realAttributeName = stripedAttribute[0];
		Object object = getAttributeOrCreateEmptyObject(target, realAttributeName);
		tryToSetAttributeByReflection(target, realAttributeName, object);
		setAttribute(object, stripedAttribute[1], value);
	}
	
	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws InstantiationException
	 */
	public static Object getAttributeOrCreateEmptyObject( Object target, String attribute ) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		Object object = tryGetAttributeByReflection(target, attribute);
		if ( object == null ) {
			Class<?> returnType = extractReturnTypeFor(target, attribute);
			object = returnType.newInstance();
		}
		return object;
	}

	/**
	 * @param target
	 * @param attribute
	 * @param value
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public static void tryToSetAttributeByReflection(Object target,
			String attribute, Object value) throws IllegalAccessException,
			InvocationTargetException, NoSuchFieldException
	{
		if (value != null) {
			Method setter = extractSetterFor(target, attribute, value.getClass());
			if (setter != null) {
				setter.invoke(target, value);
				return;
			}
		}

		Field field = extractFieldFor(target.getClass(), attribute);
		field.setAccessible(true);
		field.set(target, value);
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Field extractFieldFor(Object target, String attribute)
			throws NoSuchFieldException {
		Class<? extends Object> clazz = target.getClass();
		return extractFieldFor(clazz, attribute);
	}

	/**
	 * @param clazz
	 * @param attribute
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Field extractFieldFor(Class<?> clazz, String attribute) {
		if (clazz.equals(Object.class))
			return null;
		try {
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
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Class<?> extractReturnTypeFor(Object target, String attribute)
			throws SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException {
		if ( target == null )
			return null;
		String[] stripedAttribute = stripAttribute(attribute);
		if (stripedAttribute.length == 1)
			return tryToExtractFieldReturnTypeByReflection(target, attribute);
		
		Class<?> clazz = tryToExtractFieldReturnTypeByReflection( target, stripedAttribute[0] );
		Object newInstance = newInstanceOf(clazz);
		return extractReturnTypeFor(newInstance, stripedAttribute[1]);
	}

	/**
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	public static Object newInstanceOf(Class<?> clazz)
			throws InstantiationException, IllegalAccessException {
		if (List.class.isAssignableFrom(clazz))
			return new ArrayList();
		return clazz.newInstance();
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Class<?> tryToExtractFieldReturnTypeByReflection(Object target, String attribute)
			throws NoSuchFieldException {
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
	 * @throws NoSuchMethodException
	 */
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

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws SecurityException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchFieldException
	 */
	public static Method extractSetterFor(Object target, String attribute)
			throws SecurityException, InstantiationException, IllegalAccessException {
		try {
			return extractSetterFor(target, attribute,
					extractReturnTypeFor(target, attribute));
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	/**
	 * @param target
	 * @param attribute
	 * @param returnTypeFor
	 * @return
	 * @throws SecurityException
	 */
	public static Method extractSetterFor(Object target, String attribute,
			Class<?> returnTypeFor) throws SecurityException {
		String setter = String.format("set%s%s", attribute.substring(0, 1)
				.toUpperCase(), attribute.substring(1));
		try {
			return target.getClass().getMethod(setter, returnTypeFor);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
	/**
	 * @param annotation
	 * @param clazz
	 * @return
	 */
	public static List<Field> extractAnnotatedFieldsFor(
			Class<? extends Annotation> annotation,
			Class<? extends Object> clazz) {
		ArrayList<Field> fields = new ArrayList<Field>();
		while (!clazz.equals(Object.class)) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation))
					fields.add(field);
			}
			clazz = clazz.getSuperclass();
		}

		return fields;
	}
	
	/**
	 * @param annotation
	 * @param clazz
	 * @return
	 */
	public static List<Field> extractFieldsThatImplementsOrExtends(
			Class<?> expectedClass,
			Class<? extends Object> clazz) {
		ArrayList<Field> fields = new ArrayList<Field>();
		while (!clazz.equals(Object.class)) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.getType().isAssignableFrom(expectedClass))
					fields.add(field);
			}
			clazz = clazz.getSuperclass();
		}

		return fields;
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException 
	 */
	public static Type[] extractGenericReturnTypeFor(Object target, String attribute)
			throws SecurityException, NoSuchFieldException {
		Field field = extractFieldFor(target, attribute);
		return extractGenericReturnTypeFor ( field );
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException 
	 */
	public static Type[] extractGenericReturnTypeFor(Class<?> target, String attribute)
			throws SecurityException, NoSuchFieldException {
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
	 * @param clazz
	 * @return
	 */
	public static Set<Field> extractClassFields( Class<?> clazz ) {
		Set<Field> fields = new LinkedHashSet<Field>();

		while( !Object.class.equals(clazz) ){
			for ( Field field : clazz.getDeclaredFields() )
				fields.add( field );
			clazz = clazz.getSuperclass();
		}

		return fields;
	}

	/**
	 * @param annotation
	 * @param target
	 * @return
	 */
	public static List<Method> extractAnnotatedMethodsFor(
			Class<? extends Annotation> annotation, Object target) {
		Class<? extends Object> clazz = target.getClass();
		return extractAnnotatedMethodsFor(annotation, clazz);
	}

	/**
	 * @param annotation
	 * @param clazz
	 * @return
	 */
	public static List<Method> extractAnnotatedMethodsFor(
			Class<? extends Annotation> annotation,
			Class<? extends Object> clazz) {
		ArrayList<Method> methods = new ArrayList<Method>();

		while (!clazz.equals(Object.class)) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(annotation))
					methods.add(method);
			}
			clazz = clazz.getSuperclass();
		}

		return methods;
	}
}


//public class Reflection {
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @return
//	 * @throws InvocationTargetException 
//	 * @throws IllegalAccessException 
//	 * @throws IllegalArgumentException 
//	 */
//	public static Object getAttribute ( Object target, String attribute ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
//		Method getter = extractGetterFor(target, attribute);
//		if (getter != null)
//			return getter.invoke(target);
//
//		Field field = extractFieldFor(target.getClass(), attribute);
//
//		if ( field == null )
//			return null;
//
//		field.setAccessible(true);
//		return field.get(target);
//	}
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @return
//	 * @throws IllegalArgumentException
//	 * @throws IllegalAccessException
//	 * @throws InvocationTargetException
//	 * @throws InstantiationException
//	 */
//	public static String getAttributeAndEncodeReturnedValue ( Object target, String attribute ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
//		Field field = extractFieldFor(target, attribute);
//		if ( field != null )
//			return (String)getAttributeAndEncodeReturnedValue(target, attribute, field);
//		else {
//			Method getter = extractGetterFor(target, attribute);
//			if (getter != null)
//				return (String)getter.invoke(target);
//		}
//
//		return null;
//	}
//
//	public static Object getAttributeAndEncodeReturnedValue(Object target, String attribute, Field field)
//			throws IllegalAccessException, InvocationTargetException,
//			InstantiationException {
//		Class<?> type = field.getType();
//		Converter annotation = field.getAnnotation(Converter.class);
//		Object object = getAttribute(target, attribute);
//
//		if (!String.class.equals(type) || annotation != null) {
//			IConverter converter = (annotation != null)
//					? annotation.value().newInstance() : new DefaultDataParser();
//			object = converter.encode(object);
//		}
//		return object;
//	}
//
//	/**
//	 * Retrieves the attribute from object. If it returns null, it will create an empty
//	 * instance from its attribute and return it.
//	 * 
//	 * @param target
//	 * @param attribute
//	 * @return
//	 * @throws InvocationTargetException 
//	 * @throws IllegalAccessException 
//	 * @throws IllegalArgumentException 
//	 * @throws NoSuchFieldException 
//	 * @throws SecurityException 
//	 * @throws InstantiationException 
//	 * 		When using inner classes see: http://stackoverflow.com/a/2098009/548685
//	 */
//	public static Object getOrCreateEmptyAttribute ( Object target, String attribute )
//			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
//				   SecurityException, NoSuchFieldException, InstantiationException {
//		Object object = getAttribute(target, attribute);
//		if ( object != null )
//			return object;
//		
//		Class<?> returnType = extractReturnTypeFor(target, attribute);
//		// FIXME: nowadays, it doesn't supports Inner Classes at all
//		object = returnType.newInstance();
//		setAttribute(target, attribute, object);
//		
//		return object;
//	}
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @param value
//	 * @throws SecurityException
//	 * @throws NoSuchFieldException
//	 * @throws IllegalArgumentException
//	 * @throws IllegalAccessException
//	 * @throws InvocationTargetException
//	 */
//	public static void setAttribute ( Object target, String attribute, Object value ) throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
//		if ( value != null ) {
//			Method setter = extractSetterFor(target, attribute, value.getClass());
//			if ( setter != null ) {
//				setter.invoke(target, value);
//				return;
//			}
//		}
//
//		Field field = extractFieldFor(target.getClass(), attribute);
//		if ( field == null )
//			return;
//
//		field.setAccessible(true);
//		field.set(target, value);
//	}
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @param value
//	 * @throws IllegalAccessException
//	 * @throws InvocationTargetException
//	 * @throws InstantiationException
//	 */
//	public static void decodeValueAndSetAttribute(Object target, String attribute, Object value)
//			throws IllegalAccessException, InvocationTargetException, InstantiationException {
//		
//		Field field = extractFieldFor(target, attribute);
//		Class<?> type = field.getType();
//		Converter annotation = field.getAnnotation(Converter.class);
//		Object parsedValue = value;
//		
//		if (!String.class.equals(type) || annotation != null) {
//			IConverter converter = (annotation != null) ? annotation.value().newInstance() : new DefaultDataParser();
//			
//			parsedValue =  converter.decode(
//					parsedValue, type,
//					Reflection.extractGenericReturnTypeFor(field)
//				);
//		}
//
//		Reflection.setAttribute(target, attribute, parsedValue);
//	}
//	
//	/**
//	 * @param annotation
//	 * @param target
//	 * @return
//	 */
//	public static List<Field>  extractAnnotatedFieldsFor(Class<? extends Annotation> annotation, Object target) {
//		Class<? extends Object> clazz = target.getClass();
//		return extractAnnotatedFieldsFor(annotation, clazz);
//	}
//
//	public static List<Field> extractAnnotatedFieldsFor(
//			Class<? extends Annotation> annotation,
//			Class<? extends Object> clazz) {
//		ArrayList<Field> fields = new ArrayList<Field>();
//		while (!clazz.equals(Object.class)) {
//			for (Field field : clazz.getDeclaredFields()) {
//				if (field.isAnnotationPresent(annotation))
//					fields.add(field);
//			}
//			clazz = clazz.getSuperclass();
//		}
//
//		return fields;
//	}
//
//	public static List<Method> extractAnnotatedMethodsFor(
//			Class<? extends Annotation> annotation, Object target) {
//		Class<? extends Object> clazz = target.getClass();
//		return extractAnnotatedMethodsFor(annotation, clazz);
//	}
//
//	public static List<Method> extractAnnotatedMethodsFor(
//			Class<? extends Annotation> annotation,
//			Class<? extends Object> clazz) {
//		ArrayList<Method> methods = new ArrayList<Method>();
//
//		while (!clazz.equals(Object.class)) {
//			for (Method method : clazz.getDeclaredMethods()) {
//				if (method.isAnnotationPresent(annotation))
//					methods.add(method);
//			}
//			clazz = clazz.getSuperclass();
//		}
//
//		return methods;
//	}
//
//	/**
//	 * @param attribute
//	 * @param target
//	 * @return
//	 * @throws SecurityException
//	 * @throws NoSuchFieldException
//	 */
//	public static Annotation[] extractAnnotationsFor(String attribute, Object target)
//			throws SecurityException, NoSuchFieldException {
//		Field field = extractFieldFor(target, attribute);
//
//		if (field == null)
//			return new Annotation[]{};
//
//		return field.getAnnotations();
//	}
//
//	/**
//	 * @param attribute
//	 * @param target
//	 * @param annotation
//	 * @return
//	 * @throws SecurityException
//	 */
//	public static Annotation extractAnnotationFor(String attribute, Object target, Class<? extends Annotation> annotation)
//			throws SecurityException {
//		Field field = extractFieldFor(target, attribute);
//
//		if (field == null)
//			return null;
//
//		return field.getAnnotation(annotation);
//	}
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @return
//	 */
//	public static Field extractFieldFor (Object target, String attribute) {
//		Class<? extends Object> clazz = target.getClass();
//		return extractFieldFor(clazz, attribute);
//	}
//
//	/**
//	 * @param clazz
//	 * @param attribute
//	 * @return
//	 */
//	public static Field extractFieldFor (Class<?> clazz, String attribute) {
//		try {
//			if (clazz.equals(Object.class))
//				return null;
//			return clazz.getDeclaredField(attribute);
//		} catch (NoSuchFieldException e) {
//			return extractFieldFor(clazz.getSuperclass(), attribute);
//		}
//	}
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @return
//	 * @throws SecurityException
//	 * @throws NoSuchFieldException
//	 */
//	public static Class<?> extractReturnTypeFor (Object target, String attribute) 
//			throws SecurityException {
//		Field field = extractFieldFor(target, attribute);
//		if (field == null)
//			return null;
//		return field.getType();
//	}
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @return
//	 * @throws SecurityException
//	 */
//	public static Type[] extractGenericReturnTypeFor(Object target, String attribute)
//			throws SecurityException {
//		Field field = extractFieldFor(target, attribute);
//		return extractGenericReturnTypeFor ( field );
//	}
//	
//	/**
//	 * @param field
//	 * @return
//	 */
//	public static Type[] extractGenericReturnTypeFor(Field field) {
//		if (field == null)
//			return new Type[]{};
//		Type type = field.getGenericType();
//		if (!(type instanceof ParameterizedType))
//			return new Type[]{};
//		ParameterizedType ptype = (ParameterizedType) type;
//		return ptype.getActualTypeArguments();
//	}
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @return
//	 * @throws SecurityException
//	 * @throws NoSuchMethodException
//	 */
//	public static Method extractGetterFor(Object target, String attribute)
//			throws SecurityException {
//		String getter = String.format("get%s%s",
//				attribute.substring(0, 1).toUpperCase(),
//				attribute.substring(1));
//		try {
//			return target.getClass().getMethod(getter);
//		} catch (NoSuchMethodException e) {
//			try {
//				return target.getClass().getMethod(getter.replaceFirst("get", "is"));
//			} catch (NoSuchMethodException e1) {
//				return null;
//			}
//		}
//	}
//	
//	/**
//	 * @param target
//	 * @param attribute
//	 * @return
//	 * @throws SecurityException
//	 * @throws NoSuchFieldException
//	 */
//	public static Method extractSetterFor(Object target, String attribute) throws SecurityException, NoSuchFieldException {
//		return extractSetterFor(target, attribute,
//				extractReturnTypeFor(target, attribute));
//	}
//
//	/**
//	 * @param target
//	 * @param attribute
//	 * @param returnTypeFor
//	 * @return
//	 * @throws SecurityException
//	 */
//	public static Method extractSetterFor(Object target, String attribute, Class<?> returnTypeFor) throws SecurityException {
//		String setter = String.format("set%s%s",
//				attribute.substring(0, 1).toUpperCase(),
//				attribute.substring(1));
//		try {
//			return target.getClass().getMethod(setter, returnTypeFor);
//		} catch (NoSuchMethodException e) {
//			return null;
//		}
//	}
//
//	/**
//	 * Instantiate the className
//	 * @param className
//	 * @return
//	 */
//	public static Object instanceForName (String className) {
//		try {
//			Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
//			return clazz.newInstance();
//		} catch (Exception e) {
//			return null;
//		}
//	}
//}
