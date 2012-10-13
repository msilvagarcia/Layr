package layr.core.expressions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.regex.Matcher;

import javax.servlet.ServletException;

import layr.core.RequestContext;
import layr.core.test.stubs.StubsFactory;

import org.junit.Before;
import org.junit.Test;


public class BooleanEquationsTest {
	
	private RequestContext layrContext;

	@Before
	public void setup() throws IOException, ClassNotFoundException, ServletException{
		System.setProperty("layr.config.equationDisabled", "false");
		layrContext = StubsFactory.createFullRequestContext();
	}

	@Test
	public void assertThatAMultiBlockEquationWorks() {
		layrContext.put("age", 31);
		String expression = "#{age} >= 18 and #{age} < 20 or #{age} == 31";
		Object result = ComplexExpressionEvaluator.getValue(expression, layrContext);
		assertEquals(true, result);
	}

	@Test
	public void assertThatAMultiBlockEquationWorksAndMatchesFalse() {
		layrContext.put("age", 31);
		String expression = "#{age} >= 18 and #{age} < 20";
		Object result = ComplexExpressionEvaluator.getValue(expression, layrContext);
		assertFalse((Boolean)result);
	}

	@Test
	public void assertThatMatchesAGreaterOrEqualsEquationWorks() {
		layrContext.put("age", 18);
		String expression = "#{age} >= 18";
		Object result = ComplexExpressionEvaluator.getValue(expression, layrContext);
		assertFalse(((Integer)18).equals(result));
		assertEquals(true, result);
	}

	@Test
	public void assertThatMatchesAGreaterEquationWorks() {
		layrContext.put("age", 18);
		String expression = "#{age} > 17";
		Object result = ComplexExpressionEvaluator.getValue(expression, layrContext);
		assertFalse(((Integer)18).equals(result));
		assertEquals(true, result);
	}

	@Test
	public void assertThatMatchesAnEqualsEquationWorks() {
		layrContext.put("age", 18);
		String expression = "#{age} == 18";
		Object result = ComplexExpressionEvaluator.getValue(expression, layrContext);
		assertFalse(((Integer)18).equals(result));
		assertEquals(true, result);
	}

	@Test
	public void assertThatMatchesALowerOrEqualsEquationWorks() {
		layrContext.put("age", 15);
		String expression = "#{age} <= 18";
		Object result = ComplexExpressionEvaluator.getValue(expression, layrContext);
		assertFalse(((Integer)18).equals(result));
		assertEquals(true, result);
	}

	@Test
	public void assertThatMatchesALowerEquationWorks() {
		layrContext.put("age", 15);
		String expression = "#{age} < 17";
		Object result = ComplexExpressionEvaluator.getValue(expression, layrContext);
		assertFalse(((Integer)18).equals(result));
		assertEquals(true, result);
	}

	@Test
	public void assertThatMatchesADifferentEquationWorks() {
		layrContext.put("age", 15);
		String expression = "#{age} != 15";
		Object result = ComplexExpressionEvaluator.getValue(expression, layrContext);
		assertFalse(((Integer)18).equals(result));
		assertEquals(false, result);
	}
	
	@Test
	public void grantThatFindValidEquationComparatorInsideExpressionWithEvaluatorREGEXP() {
		String equation = "#{age} >= 18";
		Matcher matcher = ComplexExpressionEvaluator.getMatcher(
				ComplexExpressionEvaluator.RE_EQUATION_EXPRESSION_COMPARATOR, equation);
		assertTrue(matcher.find());
		assertEquals(">=", matcher.group());
	}

	@Test
	public void grantThatMatchesAnEquation() {
		String equation = "#{age} >= 18";
		Matcher matcher = ComplexExpressionEvaluator.getMatcher(ComplexExpressionEvaluator.RE_IS_EQUATION, equation);
		assertTrue(matcher.matches());
		assertEquals("#{age}", matcher.group(1).trim());
		assertEquals(">=", matcher.group(2));
		assertEquals("18", matcher.group(3).trim());
	}
	
	@Test
	public void grantThatNegativeEvaluatorWorks() {
		layrContext.put("valid", true);
		String expression = "!#{valid}";
		assertTrue(expression.matches(ComplexExpressionEvaluator.RE_IS_NEGATIVE_EQUATION));
		assertEquals(false, ComplexExpressionEvaluator.evaluateAsEquationMember(expression, layrContext));
		assertFalse((Boolean)ComplexExpressionEvaluator.getValue(expression, layrContext));	
	}
	
	@Test
	public void grantThatWorksWhenExpressionIsNull(){
		layrContext.put("age", null);
		String expression = "#{age} == null";
		assertTrue((Boolean)ComplexExpressionEvaluator.getValue(expression, layrContext));
	}
	
	@Test
	public void grantThatWorksWhenExpressionIsEmpty(){
		layrContext.put("age", "");
		String expression = "#{age} == ''";
		assertTrue((Boolean)ComplexExpressionEvaluator.getValue(expression, layrContext));
	}

	@Test
	public void grantThatWorksWhenExpressionShouldReturnTrueIfAgeIsNotNullNorEmpty(){
		String expression = "#{age} != '' and #{age} != null";

		layrContext.put("age", "123");
		assertTrue((Boolean)ComplexExpressionEvaluator.getValue(expression, layrContext));

		layrContext.put("age", null);
		assertFalse((Boolean)ComplexExpressionEvaluator.getValue(expression, layrContext));
	}
	
	@Test
	public void grantThatJavaNegativeReturnsFalse() {
		assertFalse(!"x".equals("x"));
	}
	
	@Test
	public void logicTest() {
		int x = 54;
		assertTrue( ( x > 20 && x < 30 || x == 54 ) );
		assertTrue( ( x > 20 && x < 30 || x != 54 || x == 54 && false || true ) );
	}
	
}
