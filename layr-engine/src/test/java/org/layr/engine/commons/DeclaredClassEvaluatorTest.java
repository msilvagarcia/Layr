package org.layr.engine.commons;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.layr.engine.IRequestContext;
import org.layr.engine.expressions.ExpressionEvaluator;
import org.layr.engine.sample.StubRequestContext;

public class DeclaredClassEvaluatorTest {
	private static final String CONTEXT_EXPRESSION = "#{user.context}";
	private static final String NICK_EXPRESSION = "#{user.nicknames}";
	private Usuario user;

	@Before
	public void configure() throws IOException {
		ArrayList<String> nicks = new ArrayList<String>();
		nicks.add("Jojo");
		nicks.add("Joseph");
		nicks.add("Joseh");

		IRequestContext context = new StubRequestContext() ;

		user = new Usuario();
		user.setContext(context);
		user.setNicknames(nicks);

		context.put("user", user);
	}

	@Test
	public void grantThatRetrieveTheCorrectDeclaredClass() {
		Type clazz = ExpressionEvaluator.eval(this, CONTEXT_EXPRESSION).getDeclaredClass();
		assertEquals(IRequestContext.class, clazz);

		clazz = ExpressionEvaluator.eval(this, NICK_EXPRESSION).getDeclaredClass();
		assertEquals(ArrayList.class, clazz);

		clazz = ExpressionEvaluator.eval(this, NICK_EXPRESSION).getDeclaredGenericType()[0];
		assertEquals(String.class, clazz);
	}

	public class Usuario {
		public IRequestContext context;
		private ArrayList<String> nicknames;

		public void setContext(IRequestContext context) {
			this.context = context;
		}

		public IRequestContext getContext() {
			return context;
		}

		public void setNicknames(ArrayList<String> nicknames) {
			this.nicknames = nicknames;
		}

		public ArrayList<String> getNicknames() {
			return nicknames;
		}
	}
}
