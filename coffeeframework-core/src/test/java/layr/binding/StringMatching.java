package layr.binding;

import java.util.Map;

import junit.framework.Assert;
import layr.binding.ComplexExpressionEvaluator;
import layr.util.StringUtil;

import org.junit.Test;


public class StringMatching {

	@Test
	public void assertThatMatchesJose() {
		String expected = "osé";
		String actual = StringUtil.match("José", "ose");
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void assertThatReplacesTheUrl() {
		String pattern = "users/#{id}/edit";
		String actual = ComplexExpressionEvaluator.parseMethodUrlPatternToRegExp(pattern);
    	Assert.assertEquals("users/(.*)/edit", actual);
	}
	
	@Test
	public void assertThatNotReplacesTheUrl() {
		String pattern = "users/987654/edit";
		String actual = ComplexExpressionEvaluator.parseMethodUrlPatternToRegExp(pattern);
    	Assert.assertEquals(pattern, actual);
	}
	
	@Test
	public void assertExtractCorrectPlaceHoldersFromUrlAgainsAPattern() {
		String url = "users/123/edit/Miere";
		String pattern = "users/#{id}/edit/#{name}";
		Map<String, String> values = ComplexExpressionEvaluator.extractMethodPlaceHoldersValueFromURL(pattern, url);
		Assert.assertEquals(2, values.size());
		Assert.assertEquals("123", values.get("id"));
		Assert.assertEquals("Miere", values.get("name"));
	}
	
}
