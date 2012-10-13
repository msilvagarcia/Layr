package layr.core.expressions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import layr.core.sample.Hello;
import layr.core.sample.World;

import org.junit.Test;



public class EvaluatorTest {

	private static final String HELLO_WORLD = "#{world}";
	private static final String WORLD_SIZE = "#{size}";
	private static final String HELLO_REAL_WORLD = "#{realworld.name}";
	private static final String EXTREME_REAL_WORLD_EXPRESSION = "#{realworld.hello.realworld.name}";
	private static final String REAL_WORLD_EXPRESSION = "#{realworld.id}";

	@Test
	public void grantThatTheRegExpFindAttributes(){
		String expression = HELLO_REAL_WORLD;
		assertTrue(expression.matches(ExpressionEvaluator.RE_IS_VALID_EXPRESSION));
		
		Matcher matcher = Pattern.compile(ExpressionEvaluator.RE_FIND_ATTR).matcher(expression);
		assertNotNull(matcher);
		assertTrue(matcher.find());
		assertEquals("realworld", matcher.group());
		assertTrue(matcher.find());
		assertEquals("name", matcher.group());
	}

	@Test
	public void grantThatItRetrievesTheAttribute() {
		Hello target = new Hello();
		target.setWorld("Earth");

		ExpressionEvaluator evaluator = ExpressionEvaluator.eval(target, HELLO_WORLD);
		Object value = evaluator.getValue();
		assertEquals("Earth", value);
	}

	@Test
	public void grantThatItSentsTheAttribute() {
		Hello hello = new Hello();
		ExpressionEvaluator evaluator = ExpressionEvaluator.eval(hello, HELLO_WORLD);
		String expected = "Jupter";
		evaluator.setValue(expected);
		assertEquals(expected, hello.getWorld());
	}

	@Test
	public void grantThatretrievesThreeLevelAttributes() {
		String originalName = "Mercury";
		World realworld = new World();
		realworld.setName(originalName);
		Hello hello = new Hello();
		hello.setRealworld(realworld);
		
		ExpressionEvaluator evaluator = ExpressionEvaluator.eval(hello, HELLO_REAL_WORLD);
		
		Object value = evaluator.getValue();
		assertEquals(originalName, value);
		
		String expected = "Jupter";
		evaluator.setValue(expected);
		assertEquals(expected, hello.getRealworld().getName());
	}

	@Test
	public void grantThatSetThreeLevelAttributeButCreateObjectWhenNeeded() {
		String expected = "Mercury";
		Hello hello = new Hello();
		
		ExpressionEvaluator evaluator = ExpressionEvaluator.eval(hello, HELLO_REAL_WORLD);
		evaluator.setValue(expected);
		assertNotNull("Member not created automatically", hello.getRealworld());
		assertEquals(expected, hello.getRealworld().getName());
	}

	@Test
	public void grantThatSetThreeLevelDeclaredClass() {
		Hello hello = new Hello();
		
		ExpressionEvaluator evaluator = ExpressionEvaluator.eval(hello, EXTREME_REAL_WORLD_EXPRESSION);
		assertEquals(String.class, evaluator.getDeclaredClass());
		evaluator = ExpressionEvaluator.eval(hello, REAL_WORLD_EXPRESSION);
		assertEquals(Long.class, evaluator.getDeclaredClass());
	}

	@Test
	public void grantThatDefaultFieldParserConvertToInteger(){
		Hello hello = new Hello();
		ExpressionEvaluator evaluator = ExpressionEvaluator.eval(hello, WORLD_SIZE);
		Integer size = 123456;
		evaluator.setValue(size);
		assertEquals(size, hello.getSize());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void grantReflectionCanExtractGenericClass() throws SecurityException, NoSuchFieldException {
		Hello hello = new Hello();
		Field field = hello.getClass().getDeclaredField("countries");
		ParameterizedType ptype = (ParameterizedType) field.getGenericType();
		assertEquals(String.class, (Class)ptype.getActualTypeArguments()[0]);
	}
}
