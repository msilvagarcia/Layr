/**
 * Copyright 2011 Miere Liniel Teixeira
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
package org.layr.engine.expressions;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.layr.commons.Reflection;
import org.layr.commons.StringUtil;



/**
 * Evaluates an expression against an object.
 * @author Miere Teixeira
 */
public class ExpressionEvaluator {

	public static final String EMPTY_EXPRESSION = "#{}";
	public static final String RE_EXTRACT_PLACEHOLDER = "[\\w:]+";
	public static final String RE_IS_VALID_EXPRESSION = "#\\{[\\w:]+?[\\w\\.]+?\\}";
	public static final String RE_IS_VALID_SINGLE_RETRIEVABLE_EXPRESSION = "^#\\{[\\w:]+?[\\w\\.]+?\\}$";
	public static final String RE_FIND_ATTR = "\\w[\\w\\d:]*";

	private Object target;
	private String expression;

	/**
	 * 
	 */
	public ExpressionEvaluator() {
	}

	/**
	 * @param target
	 * @param expression
	 */
	public ExpressionEvaluator(Object target, String expression) {
		this.setTarget(target);
		this.setExpression(expression);
	}

	/**
	 * @param expression
	 * @return
	 */
	public static boolean isValidExpression(String expression) {
		if (StringUtil.isEmpty(expression))
			return false;
		return expression.matches(RE_IS_VALID_EXPRESSION);
	}

	/**
	 * @param expression
	 * @return
	 */
	public static String extractObjectPlaceholder(String expression) {
		Matcher matcher = Pattern.compile(RE_EXTRACT_PLACEHOLDER).matcher(expression);
		if (matcher.find())
			return matcher.group();
		return null;
	}
	
	/**
	 * @return
	 */
	public Class<?> getDeclaredClass() {
		Field field = getField();
		return field.getType();
	}

	/**
	 * @return
	 */
	public Field getField() {
		if (!isValidExpression(expression))
			return null;

		Matcher matcher = Pattern.compile(ExpressionEvaluator.RE_FIND_ATTR).matcher(expression);
		
		Field field = null;
		Class<?> clazz = this.target.getClass();

		while(matcher.find() && clazz != null){
			String attribute = matcher.group();
			field = Reflection.extractFieldFor(clazz, attribute);
			clazz = field != null ? field.getType() : null;
		}
		return field;
	}

	/**
	 * @return
	 */
	public Type[] getDeclaredGenericType() {
		Field field = getField();
		return Reflection.extractGenericReturnTypeFor(field);
	}

	/**
	 * @return
	 */
	public Object getValue() {
		return getValue(false);
	}

	/**
	 * @return
	 */
	public Object getValue(boolean shouldBeEnconded) {
		if (!isValidExpression(expression))
			return null;

		Matcher matcher = Pattern.compile(ExpressionEvaluator.RE_FIND_ATTR).matcher(expression);

		if (!matcher.find())
			return null;

		return getValue(this.target, matcher, shouldBeEnconded);
	}

	/**
	 * @param target
	 * @param matcher
	 * @param shouldBeEnconded 
	 * @return
	 */
	public Object getValue(Object target, Matcher matcher, boolean shouldBeEnconded) {
		String attribute = matcher.group();
		try {
			if (Properties.class.isInstance(target))
				return ((Properties)target).get(attribute);

			if (matcher.find()) {
				Object object = Reflection.getAttribute(target, attribute);
				return getValue(object, matcher, shouldBeEnconded);
			} else if ( shouldBeEnconded ) {
				return Reflection.getAttributeAndEncodeReturnedValue(target, attribute);
			} else 
				return Reflection.getAttribute(target, attribute);
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param value
	 * @return
	 */
	public boolean setValue(Object value) {
		if (!isValidExpression(expression))
			return false;

		Matcher matcher = Pattern.compile(ExpressionEvaluator.RE_FIND_ATTR).matcher(expression);

		if (!matcher.find())
			return false;

		return this.setValue(target, matcher, value);
	}

	/**
	 * @param target
	 * @param matcher
	 * @param value
	 * @return true if it can set the value
	 */
	public boolean setValue(Object target, Matcher matcher, Object value) {
		String attribute = matcher.group();
		try {
			if (!matcher.find()) {
				Reflection.decodeValueAndSetAttribute(target, attribute, value);
				return true;
			} else {
				target = Reflection.getOrCreateEmptyAttribute(target, attribute);
				return setValue(target, matcher, value);
			}
		} catch (Exception e) {
//			System.err.println("WARN: " + e.getMessage());
//			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param target
	 * @param expression
	 * @return
	 */
	public static ExpressionEvaluator eval(Object target, String expression) {
		return new ExpressionEvaluator(target, expression);
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTarget() {
		return target;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}
}
