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
package layr.binding;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import layr.RequestContext;
import layr.util.Dictionary;


public class ComplexExpressionEvaluator {

	// Equation Support Constants
	public static final String RE_EQUATION_EXPRESSION_COMPARATOR = "(<=|>=|<|==|!=|>)";
	public static final String RE_EQUATION_CONCATENATION_DELIMITER = "(&&|\\|\\||$)";
	public static final String RE_EQUATION_NOT_OPERATOR = "^( *! *)";
	public static final String RE_IS_NEGATIVE_EQUATION = 
			RE_EQUATION_NOT_OPERATOR + "("+ExpressionEvaluator.RE_IS_VALID_EXPRESSION+"| *true *| *false *)";
	public static final String RE_IS_EQUATION = "(.*)"+RE_EQUATION_EXPRESSION_COMPARATOR+"(.*)";
	private static final String AND = "&&";
	private static final String OR = "||";

	private static Map<String, Pattern> patternCache;
	
	public static Object getValue(String expression, RequestContext context) {
		return getValue(expression, context, false);
	}

	/**
	 * Try to retrieve a value that corresponds to the expression.
	 * 
	 * @param expression
	 * @param context
	 * @return
	 */
	public static Object getValue(String expression, RequestContext context, boolean shouldBeEnconded) {
		if (expression == null)
			return null;

		Matcher matcher = null;

		if ( !context.getApplicationContext().getLayrConfiguration().isEquationsDisabled() ) {
			if ( expression.matches(RE_IS_NEGATIVE_EQUATION) )
				return evaluateAsEquationMember(expression, context);

			if ( expression.matches(".+"+RE_EQUATION_CONCATENATION_DELIMITER+".+") )
				return evaluateMultiBlockEquation(expression, context);

			matcher = ComplexExpressionEvaluator.getMatcher(RE_IS_EQUATION, expression);
			if (matcher.matches())
				return evaluateEquation(matcher, context);
		}

		matcher = getMatcher(ExpressionEvaluator.RE_IS_VALID_SINGLE_RETRIEVABLE_EXPRESSION, expression);
		if (matcher.find())
			return evaluateExpressionAsObject(matcher, context, shouldBeEnconded);
		return evaluateExpressionAsString(expression, context);
	}

	/**
	 * @param matcher
	 * @param context
	 * @param equation
	 * @return
	 * @throws RuntimeException
	 */
	public static Boolean evaluateMultiBlockEquation(String equation, RequestContext context)
			throws RuntimeException {
		
		Matcher matcher = ComplexExpressionEvaluator.getMatcher(RE_EQUATION_CONCATENATION_DELIMITER, equation);

		String expression = null , comparator = OR;
		boolean lastResult = false, actualResult;
		int begin=0, end=0;
		
		 while(matcher.find()) {
			end = matcher.start();
			expression = equation.substring(begin, end);
			begin = matcher.end();

			Matcher expressionMatcher = ComplexExpressionEvaluator.getMatcher(RE_IS_EQUATION, expression);
			if (!expressionMatcher.matches())
				throw new RuntimeException("Invalid Expression: " + expression);

			actualResult = evaluateEquation(expressionMatcher, context);
			if (OR.equals(comparator))
				lastResult = ( lastResult || actualResult );
			else if (AND.equals(comparator))
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
	public static Boolean evaluateEquation(Matcher matcher, RequestContext context) {
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
	public static Object evaluateAsEquationMember(String member, RequestContext context) {

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
    public static String parseMethodUrlPatternToRegExp ( String pattern ) {
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
    public static Map<String, String> extractMethodPlaceHoldersValueFromURL( String pattern, String url ) {
		Dictionary<String, String> placeHolders = new Dictionary<String, String>();
		String regex = ComplexExpressionEvaluator.parseMethodUrlPatternToRegExp(pattern);

		Matcher urlpatternMatcher = ComplexExpressionEvaluator.getMatcher(
    			ExpressionEvaluator.RE_IS_VALID_EXPRESSION, pattern);
		Matcher urlMatcher = ComplexExpressionEvaluator.getMatcher(regex, url);
		
		int cursor = 1;
		if (urlMatcher.find()){
			while ( cursor <= urlMatcher.groupCount() && urlpatternMatcher.find() ) {
				String placeholder = urlpatternMatcher.group();
				String value = ExpressionEvaluator.extractObjectPlaceholder(placeholder);
				placeHolders.set(value, urlMatcher.group(cursor));
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
	protected static String evaluateExpressionAsString(String expression, RequestContext context) {
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
	protected static Object evaluateExpressionAsObject(Matcher matcher, RequestContext context, boolean shouldBeEnconded) {
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
	public static Matcher getMatcher(String expression, String string) {

		if (patternCache == null)
			patternCache = new ConcurrentHashMap<String, Pattern>();
		
		Pattern pattern = patternCache.get(expression);
		if (pattern == null) {
			pattern = Pattern.compile(expression);
			patternCache.put(expression, pattern);
		}
		return pattern.matcher(string);
	}

	/**
	 * Free cache memory
	 */
	public static void clearCache () {
		if ( patternCache != null ) {
			patternCache.clear();
			patternCache = null;
		}
	}
}
