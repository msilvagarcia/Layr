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
package layr.commons;

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
		if (target == null)
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
	public static Object tryGetAttributeByReflection(Object target,
			String attribute) {
		try {
			Method getter = extractGetterFor(target, attribute);
			if (getter != null) {
				getter.setAccessible(true);
				return getter.invoke(target);
			}

			Field field = extractFieldFor(target.getClass(), attribute);
			if (field == null)
				return null;

			field.setAccessible(true);
			return field.get(target);
		} catch (Throwable e) {
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
			NoSuchFieldException, InstantiationException {
		String[] stripedAttribute = stripAttribute(attribute);
		if (stripedAttribute.length == 1) {
			tryToSetAttributeByReflection(target, attribute, value);
			return;
		}

		String realAttributeName = stripedAttribute[0];
		Object object = getAttributeOrCreateEmptyObject(target,
				realAttributeName);
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
	public static Object getAttributeOrCreateEmptyObject(Object target,
			String attribute) throws IllegalAccessException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException {
		Object object = tryGetAttributeByReflection(target, attribute);
		if (object == null) {
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
			InvocationTargetException, NoSuchFieldException {
		if (value != null) {
			Method setter = extractSetterFor(target, attribute,
					value.getClass());
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
			throws SecurityException, NoSuchFieldException,
			InstantiationException, IllegalAccessException {
		if (target == null)
			return null;
		String[] stripedAttribute = stripAttribute(attribute);
		if (stripedAttribute.length == 1)
			return tryToExtractFieldReturnTypeByReflection(target, attribute);

		Class<?> clazz = tryToExtractFieldReturnTypeByReflection(target,
				stripedAttribute[0]);
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
	public static Class<?> tryToExtractFieldReturnTypeByReflection(
			Object target, String attribute) throws NoSuchFieldException {
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
			throws SecurityException, InstantiationException,
			IllegalAccessException {
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
			Class<? extends Object> clazz,
			Class<? extends Annotation>... annotations) {
		ArrayList<Field> fields = new ArrayList<Field>();

		while (!clazz.equals(Object.class)) {
			for (Field field : clazz.getDeclaredFields()) {
				if (isAnnotationPresent(field, annotations))
					fields.add(field);
			}
			clazz = clazz.getSuperclass();
		}

		return fields;
	}

	public static boolean isAnnotationPresent(Field field,
			Class<? extends Annotation>... annotations) {
		for (Class<? extends Annotation> annotation : annotations)
			if (field.isAnnotationPresent(annotation))
				return true;
		return false;
	}

	/**
	 * @param annotation
	 * @param clazz
	 * @return
	 */
	public static List<Field> extractFieldsThatImplementsOrExtends(
			Class<?> expectedClass, Class<? extends Object> clazz) {
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
	public static Type[] extractGenericReturnTypeFor(Object target,
			String attribute) throws SecurityException, NoSuchFieldException {
		Field field = extractFieldFor(target, attribute);
		return extractGenericReturnTypeFor(field);
	}

	/**
	 * @param target
	 * @param attribute
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	public static Type[] extractGenericReturnTypeFor(Class<?> target,
			String attribute) throws SecurityException, NoSuchFieldException {
		Field field = extractFieldFor(target, attribute);
		return extractGenericReturnTypeFor(field);
	}

	/**
	 * @param field
	 * @return
	 */
	public static Type[] extractGenericReturnTypeFor(Field field) {
		if (field == null)
			return new Type[] {};
		Type type = field.getGenericType();
		if (!(type instanceof ParameterizedType))
			return new Type[] {};
		ParameterizedType ptype = (ParameterizedType) type;
		return ptype.getActualTypeArguments();
	}

	/**
	 * @param clazz
	 * @return
	 */
	public static Set<Field> extractClassFields(Class<?> clazz) {
		Set<Field> fields = new LinkedHashSet<Field>();

		while (!Object.class.equals(clazz)) {
			for (Field field : clazz.getDeclaredFields())
				fields.add(field);
			clazz = clazz.getSuperclass();
		}

		return fields;
	}

	/**
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	public static List<Method> extractAnnotatedMethodsFor(
			Class<? extends Object> clazz,
			Class<? extends Annotation>...annotations) {
		ArrayList<Method> methods = new ArrayList<Method>();

		while (!clazz.equals(Object.class)) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (isAnnotationPresent(method, annotations))
					methods.add(method);
			}
			clazz = clazz.getSuperclass();
		}

		return methods;
	}
	
	private static boolean isAnnotationPresent(
			Method method,
			Class<? extends Annotation>[] annotations) {;
		for ( Class<? extends Annotation> annotation : annotations )
			if ( method.isAnnotationPresent(annotation) )
				return true;
		return false;
	}
}
