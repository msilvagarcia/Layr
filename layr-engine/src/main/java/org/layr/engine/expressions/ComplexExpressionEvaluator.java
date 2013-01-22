/**
 * Copyright 2013 Miere Liniel Teixeira
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
		Matcher matcher = evaluator.getMatcher(
				ExpressionEvaluator.RE_IS_VALID_SINGLE_RETRIEVABLE_EXPRESSION, expression);
		if (matcher.find())
			return evaluator.evaluateExpressionAsObject(matcher, context, shouldBeEnconded);
		return evaluator.evaluateExpressionAsString(expression, context);
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
		String regex = parseMethodUrlPatternToRegExp(pattern);
		Matcher urlpatternMatcher = getMatcher( ExpressionEvaluator.RE_IS_VALID_EXPRESSION, pattern);
		Matcher urlMatcher = getMatcher(regex, url);
		return createPlaceHolderMapExtractingDataFromURL(
					urlpatternMatcher, urlMatcher);
    }

	/**
	 * @param urlpatternMatcher
	 * @param urlMatcher
	 * @return
	 */
	public Map<String, String> createPlaceHolderMapExtractingDataFromURL(Matcher urlpatternMatcher,
			Matcher urlMatcher) {
		Map<String, String> placeHolders = new HashMap<String, String>();
		int cursor = 1;
		if (urlMatcher.find()){
			while ( cursor <= urlMatcher.groupCount() && urlpatternMatcher.find() ) {
				populateMapWithPlaceHoldersExtractedFromURL(placeHolders, urlpatternMatcher, urlMatcher, cursor);
				cursor++;
			}
		}
		return placeHolders;
	}

	/**
	 * @param placeHolders
	 * @param urlpatternMatcher
	 * @param urlMatcher
	 * @param cursor
	 */
	public void populateMapWithPlaceHoldersExtractedFromURL(Map<String, String> placeHolders, Matcher urlpatternMatcher,
			Matcher urlMatcher, int cursor) {
		String value = urlMatcher.group(cursor);
		String group = urlpatternMatcher.group();
		String placeHolder = ExpressionEvaluator.extractObjectPlaceholder(group);
		placeHolders.put(placeHolder, value);
	}

	/**
	 * Try to retrieve a value that corresponds to a composed expression as String.
	 * 
	 * @param expression
	 * @param context
	 * @return
	 */
	public String evaluateExpressionAsString(String expression, IRequestContext context) {
		ArrayList<String> matchedGroups = new ArrayList<String>();
		Matcher matcher = getMatcher(ExpressionEvaluator.RE_IS_VALID_EXPRESSION, expression);

		while (matcher.find()) {
			String group = matcher.group();
			if (matchedGroups.contains(group))
				continue;

			Object evaluatedExpression = evaluateExpressionAsObjectButReturnsEmptyStringIfExpressionWasNull(context, matcher);
			expression =  expression.replace(group, evaluatedExpression.toString());
			matchedGroups.add(group);
		}

		return expression;
	}

	/**
	 * @param context
	 * @param matcher
	 * @return
	 */
	public Object evaluateExpressionAsObjectButReturnsEmptyStringIfExpressionWasNull(IRequestContext context, Matcher matcher) {
		Object evaluatedExpression = evaluateExpressionAsObject(matcher, context, true);
		if (evaluatedExpression == null)
			evaluatedExpression = "";
		return evaluatedExpression;
	}

	/**
	 * Try to retrieve a value that corresponds to a simple expression as Object.
	 * @param matcher
	 * @param context
	 * 
	 * @return
	 */
	public Object evaluateExpressionAsObject(Matcher matcher, IRequestContext context, boolean shouldBeEnconded) {
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
