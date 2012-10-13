package layr.core.expressions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import javax.servlet.ServletException;

import layr.core.RequestContext;
import layr.core.test.stubs.StubsFactory;

import org.junit.Before;
import org.junit.Test;


public class ExpressionTest {

	private static final String COMPONENT_PATTERN_NAME = "#{Component:usuario}blah";
	private static final String COMPONENT_COMPOSED_PATTERN_NAME = "#{Component:usuario.nome}blah";
	private static final String CONTEXT = "#{usuario.context}";
	private static final String HELLO = "Olá #{usuario.nome}!";
	private static final String NAME = "#{nome}";
	private static final String TWO_EXPRESSIONS = "#{usuario.nome} will #{usuario.action} till tomorrow.";

	private RequestContext layrContext;

	@Before
	public void setup() throws IOException, ClassNotFoundException, ServletException{
		layrContext = StubsFactory.createFullRequestContext();
	}

	@Test
	public void evalTest() {
		Usuario usuario = new Usuario("Miere", layrContext);
		layrContext.put("usuario", usuario);

		String returnString = ComplexExpressionEvaluator.getValue(HELLO, layrContext ).toString();
		assertEquals("Olá Miere!", returnString);

		Object evaluatedObject = ComplexExpressionEvaluator.getValue(CONTEXT, layrContext);
		assertSame(layrContext, evaluatedObject);

		String newName = "Joseh";
		//LayrBinder.setValue(NAME, context, newName);
		ExpressionEvaluator.eval(usuario, NAME).setValue(newName);
		assertEquals(newName, usuario.getNome());
	}
	
	@Test
	public void evalTwoExpressions() {
		Usuario usuario = new Usuario("Miere", layrContext);
		usuario.setAction("run");
		layrContext.put("usuario", usuario);

		String returnString = ComplexExpressionEvaluator.getValue(TWO_EXPRESSIONS, layrContext ).toString();
		assertEquals("Miere will run till tomorrow.", returnString);
	}
	
	@Test
	public void evalComponentPattern() {
		layrContext.put("Component:usuario", "blah");

		Object value = ComplexExpressionEvaluator.getValue(COMPONENT_PATTERN_NAME, layrContext );
		assertNotNull(value);
		String returnString = value.toString();
		assertEquals("blahblah", returnString);
	}
	
	@Test
	public void evalAnotherComponentPattern() {
		layrContext.put("Component:usuario", new Usuario("Miere",null));

		Object value = ComplexExpressionEvaluator.getValue(COMPONENT_COMPOSED_PATTERN_NAME, layrContext );
		assertNotNull(value);
		String returnString = value.toString();
		assertEquals("Miereblah", returnString);
	}
	
	@Test
	public void evalExpressionWhenTheresNoPlaceholderButThisIsSetAtContext(){
		String expected = "Miere";
		layrContext.put("nome", expected);
		Object value = ComplexExpressionEvaluator.getValue(NAME, layrContext );
		assertNotNull(value);
		String returnString = value.toString();
		assertEquals(expected, returnString);
	}

	public class Usuario {
		private String nome;
		private String action;
		private RequestContext context;
		
		public Usuario(String nome, RequestContext context) {
			setContext(context);
			setNome(nome);
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}

		public void setContext(RequestContext context) {
			this.context = context;
		}

		public RequestContext getContext() {
			return context;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getAction() {
			return action;
		}
	}
}
