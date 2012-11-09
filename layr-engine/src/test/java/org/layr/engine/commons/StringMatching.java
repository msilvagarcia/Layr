package org.layr.engine.commons;

import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.layr.commons.Cache;
import org.layr.commons.StringUtil;
import org.layr.engine.expressions.ComplexExpressionEvaluator;


public class StringMatching {
	
	private ComplexExpressionEvaluator evaluator;

	@Before
	public void setup(){
		evaluator = ComplexExpressionEvaluator.newInstance();
	}
	
	@After
	public void tearDown(){
		Cache.clearAllCaches();
	}

	@Test
	public void assertThatMatchesJose() {
		String expected = "osé";
		String actual = StringUtil.match("José", "ose");
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void assertThatReplacesTheUrl() {
		String pattern = "users/#{id}/edit";
		String actual = evaluator.parseMethodUrlPatternToRegExp(pattern);
    	Assert.assertEquals("users/(.*)/edit", actual);
	}
	
	@Test
	public void assertThatNotReplacesTheUrl() {
		String pattern = "users/987654/edit";
		String actual = evaluator.parseMethodUrlPatternToRegExp(pattern);
    	Assert.assertEquals(pattern, actual);
	}
	
	@Test
	public void assertExtractCorrectPlaceHoldersFromUrlAgainsAPattern() {
		String url = "users/123/edit/Miere";
		String pattern = "users/#{id}/edit/#{name}";
		Map<String, String> values = evaluator.extractMethodPlaceHoldersValueFromURL(pattern, url);
		Assert.assertEquals(2, values.size());
		Assert.assertEquals("123", values.get("id"));
		Assert.assertEquals("Miere", values.get("name"));
	}
	
}
