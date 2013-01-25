package org.layr.engine.expressions;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.util.regex.Matcher;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.layr.engine.IRequestContext;
import org.layr.engine.sample.StubRequestContext;

public class PooledExpressionTest {

	private static final String CONTEXT = "#{user.context}";
	private static final String NAME = "#{user.nome}";

	private IRequestContext layrContext;
	private ComplexExpressionEvaluator evaluator;

	@Before
	public void setup() throws IOException, ClassNotFoundException, ServletException{
		System.setProperty("layr.config.equationDisabled", "false");
		layrContext = new StubRequestContext();
		evaluator = ComplexExpressionEvaluator.newInstance();
	}

	@Test
	public void testGetValueSpeed() {
		layrContext.put("user", new User("Miere Teixeira", layrContext));
		
		long prev_time, total_time=0, loop_times=1;
		for (int j=0; j<loop_times; j++) {
		    prev_time = System.currentTimeMillis();
			for (int i=0;i<10000; i++) {
				Object value = ComplexExpressionEvaluator.getValue(CONTEXT, layrContext);
				assertSame(layrContext, value);
			}
			long time = System.currentTimeMillis() - prev_time;
			total_time+= time;
		}
		System.out.println("Total Time:" + total_time);
	}

	@Test
	public void notSameMatcher() {
		Matcher matcher = evaluator.getMatcher(ExpressionEvaluator.RE_IS_VALID_EXPRESSION, NAME);
		Matcher matcher2 = evaluator.getMatcher(ExpressionEvaluator.RE_IS_VALID_EXPRESSION, NAME);
		assertNotSame(matcher, matcher2);
	}

	public class User {
		private String nome;
		private IRequestContext context;
		
		public User(String nome, IRequestContext context) {
			setContext(context);
			setNome(nome);
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}

		public void setContext(IRequestContext context) {
			this.context = context;
		}

		public IRequestContext getContext() {
			return context;
		}
	}
}
