package layr.binding;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.ArrayList;

import layr.RequestContext;
import layr.binding.ExpressionEvaluator;

import org.junit.Before;
import org.junit.Test;



public class DeclaredClassEvaluatorTest {
	private static final String CONTEXT_EXPRESSION = "#{user.context}";
	private static final String NICK_EXPRESSION = "#{user.nicknames}";
	private Usuario user;

	@Before
	public void configure() {
		ArrayList<String> nicks = new ArrayList<String>();
		nicks.add("Jojo");
		nicks.add("Joseph");
		nicks.add("Joseh");

		RequestContext context = new RequestContext();

		user = new Usuario();
		user.setContext(context);
		user.setNicknames(nicks);

		context.put("user", user);
	}

	@Test
	public void grantThatRetrieveTheCorrectDeclaredClass() {
		Type clazz = ExpressionEvaluator.eval(this, CONTEXT_EXPRESSION).getDeclaredClass();
		assertEquals(RequestContext.class, clazz);

		clazz = ExpressionEvaluator.eval(this, NICK_EXPRESSION).getDeclaredClass();
		assertEquals(ArrayList.class, clazz);

		clazz = ExpressionEvaluator.eval(this, NICK_EXPRESSION).getDeclaredGenericType()[0];
		assertEquals(String.class, clazz);
	}

	public class Usuario {
		public RequestContext context;
		private ArrayList<String> nicknames;

		public void setContext(RequestContext context) {
			this.context = context;
		}

		public RequestContext getContext() {
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
