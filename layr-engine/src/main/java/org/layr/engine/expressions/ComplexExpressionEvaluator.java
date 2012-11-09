/**
 * Copyright 2012 Miere Liniel Teixeira
 * 
 * Many thanks to Ricardo Baumgartner <ladraum@gmail.com>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.layr.engine.IRequestContext;

public class ComplexExpressionEvaluator {

	// Equation Support Constants
	public static final String RE_EQUATION_EXPRESSION_COMPARATOR = "(<=|>=|<|==|!=|>)";
	public static final String RE_EQUATION_CONCATENATION_DELIMITER = "( and | or |$)";
	public static final String RE_EQUATION_NOT_OPERATOR = "^( *! *)";
	public static final String RE_IS_NEGATIVE_EQUATION = 
			RE_EQUATION_NOT_OPERATOR + "("+ExpressionEvaluator.RE_IS_VALID_EXPRESSION+"| *true *| *false *)";
	public static final String RE_IS_EQUATION = "(.*)"+RE_EQUATION_EXPRESSION_COMPARATOR+"(.*)";
	private static final String AND = "and";
	private static final String OR = "or";

	private ComplexExpressionEvaluator (){}
	
	public static ComplexExpressionEvaluator newInstance() {
		ComplexExpressionEvaluator evaluator = new ComplexExpressionEvaluator();
		return evaluator;
	}

	public static Object getValue(String expression, IRequestContext context) {
		return getValue(expression, context, false);
	}

	/**
	 * Try to retrieve a value that corresponds to the expression.
	 * 
	 * @param expression
	 * @param context
	 * @return
	 */
	public static Object getValue(String expression, IRequestContext context, boolean shouldBeEnconded) {
		if (expression == null)
			return null;

		ComplexExpressionEvaluator evaluator = newInstance();
		Matcher matcher = null;
		if ( expression.matches(RE_IS_NEGATIVE_EQUATION) )
			return evaluator.evaluateAsEquationMember(expression, context);

		if ( expression.matches(".+"+RE_EQUATION_CONCATENATION_DELIMITER+".+") )
			return evaluator.evaluateMultiBlockEquation(expression, context);

		matcher = evaluator.getMatcher(RE_IS_EQUATION, expression);
		if (matcher.matches())
			return evaluator.evaluateEquation(matcher, context);

		matcher = evaluator.getMatcher(ExpressionEvaluator.RE_IS_VALID_SINGLE_RETRIEVABLE_EXPRESSION, expression);
		if (matcher.find())
			return evaluator.evaluateExpressionAsObject(matcher, context, shouldBeEnconded);
		return evaluator.evaluateExpressionAsString(expression, context);
	}

	/**
	 * @param matcher
	 * @param context
	 * @param equation
	 * @return
	 * @throws RuntimeException
	 */
	public Boolean evaluateMultiBlockEquation(String equation, IRequestContext context)
			throws RuntimeException {
		
		Matcher matcher = getMatcher(RE_EQUATION_CONCATENATION_DELIMITER, equation);

		String expression = null , comparator = OR;
		boolean lastResult = false, actualResult;
		int begin=0, end=0;
		
		 while(matcher.find()) {
			end = matcher.start();
			expression = equation.substring(begin, end);
			begin = matcher.end();

			Matcher expressionMatcher = getMatcher(RE_IS_EQUATION, expression);
			if (!expressionMatcher.matches())
				throw new RuntimeException("Invalid Expression: " + expression);

			actualResult = evaluateEquation(expressionMatcher, context);
			if (OR.equals(comparator.trim()))
				lastResult = ( lastResult || actualResult );
			else if (AND.equals(comparator.trim()))
				lastResult = ( lastResult && actualResult );

			comparator = matcher.group();
		}

		return lastResult;
	}

	/**
	 * @param matcher
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean evaluateEquation(Matcher matcher, IRequestContext context) {
		Object x = evaluateAsEquationMember(matcher.group(1).trim(), context);
		Object y = evaluateAsEquationMember(matcher.group(3).trim(), context);
		String comparator = matcher.group(2);

		try {

			if ( ">=".equals(comparator) )
				return ( ((Comparable<Object>)x).compareTo(y) >= 0);
			if ( ">".equals(comparator) )
				return ( ((Comparable<Object>)x).compareTo(y) > 0);
			if ( "<=".equals(comparator) )
				return ( ((Comparable<Object>)x).compareTo(y) <= 0);
			if ( "<".equals(comparator))
				return ( ((Comparable<Object>)x).compareTo(y) < 0);
		
		} catch (NullPointerException e) {
			throw new NullPointerException("Can't check if '" + x + " " + comparator + " " + y + "' is true.");
		}

		boolean isEqualsOrSame = x == y;
		if ( "==".equals(comparator) )
			return isEqualsOrSame || x.equals(y);

		if ( x == null ) {
			x = y;
			y = null;
		}

		return !(isEqualsOrSame || x.equals(y));
	}

	/**
	 * @param member
	 * @param context
	 * @return
	 */
	public Object evaluateAsEquationMember(String member, IRequestContext context) {

		if ( "null".equals(member) )
			return null;

		Matcher matcher = getMatcher("\\d+", member);
		if (matcher.matches())
			return Integer.parseInt(member);

		matcher = getMatcher("\\d+\\.\\d+", member);
		if (matcher.matches())
			return Double.parseDouble(member);
		
		boolean negative = false;
		Object result = null;

		if (member.matches(RE_EQUATION_NOT_OPERATOR+".*")){
			negative = true;
			member = member.replaceFirst("^ *! *", "");
		}
		
		if ( "true".equals(member)
		||   "false".equals(member) )
			result = Boolean.parseBoolean(member);
		else
			result = getValue(
						member
							.replaceFirst("^'", "")
							.replaceFirst("'$", ""), context);

		if (negative && Boolean.class.isInstance(result))
			return !(Boolean)result;

		return result;
	}

    /**
     * Parse Method URL
     * @param pattern
     * @return a Regular Expression that matches the URL pattern
     */
    public String parseMethodUrlPatternToRegExp ( String pattern ) {
    	Matcher matcher = getMatcher(
    			ExpressionEvaluator.RE_IS_VALID_EXPRESSION, pattern);
    	return matcher.replaceAll("(.*)");
    }
    
    /**
     * Extract the place holders value from URL based on URL pattern
     * @param pattern
     * @param url
     * @return
     */
    public Map<String, String> extractMethodPlaceHoldersValueFromURL( String pattern, String url ) {
    	Map<String, String> placeHolders = new HashMap<String, String>();
		String regex = parseMethodUrlPatternToRegExp(pattern);

		Matcher urlpatternMatcher = getMatcher(
    			ExpressionEvaluator.RE_IS_VALID_EXPRESSION, pattern);
		Matcher urlMatcher = getMatcher(regex, url);
		
		int cursor = 1;
		if (urlMatcher.find()){
			while ( cursor <= urlMatcher.groupCount() && urlpatternMatcher.find() ) {
				String placeholder = urlpatternMatcher.group();
				String value = ExpressionEvaluator.extractObjectPlaceholder(placeholder);
				placeHolders.put(value, urlMatcher.group(cursor));
				cursor++;
			}
		}
		
		return placeHolders;
    }

	/**
	 * Try to retrieve a value that corresponds to a composed expression as String.
	 * 
	 * @param expression
	 * @param context
	 * @return
	 */
	protected String evaluateExpressionAsString(String expression, IRequestContext context) {
		ArrayList<String> matchedGroups = new ArrayList<String>();

		Matcher matcher = getMatcher(ExpressionEvaluator
				.RE_IS_VALID_EXPRESSION, expression);

		while (matcher.find()) {
			String group = matcher.group();
			if (matchedGroups.contains(group))
				continue;

			Object evaluatedExpression = evaluateExpressionAsObject(matcher, context, true);
			if (evaluatedExpression == null)
				evaluatedExpression = "";

			expression =  expression.replace(group, evaluatedExpression.toString());
			matchedGroups.add(group);
		}

		return expression;
	}

	/**
	 * Try to retrieve a value that corresponds to a simple expression as Object.
	 * @param matcher
	 * @param context
	 * 
	 * @return
	 */
	protected Object evaluateExpressionAsObject(Matcher matcher, IRequestContext context, boolean shouldBeEnconded) {
		String nextExpression = matcher.group();
		String placeholder = ExpressionEvaluator.extractObjectPlaceholder(nextExpression);
		Object targetObject = context.get(placeholder);

		if (targetObject == null)
			return null;

		nextExpression = nextExpression.replaceFirst(placeholder+"\\.?", "");
		if (nextExpression.equals(ExpressionEvaluator.EMPTY_EXPRESSION))
			return targetObject;

		ExpressionEvaluator evaluator = new ExpressionEvaluator(targetObject, nextExpression);
		return evaluator.getValue(shouldBeEnconded);
	}

	/**
	 * Retrieves a matcher from the RegExp local cache.
	 * 
	 * @param expression
	 * @param string
	 * @return
	 */
	public Matcher getMatcher(String expression, String string) {
		Pattern pattern = Pattern.compile(expression);
		return pattern.matcher(string);
	}
}
